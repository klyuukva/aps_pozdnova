import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class DeviceManager implements Runnable {
    private final ArrayList<Devices> devices;
    private final Buffer buffer;
    private volatile int indexDevicesPlace;
    private volatile int requestPointer;
    private final Object bufferNotEmptyNotifier;
    private Report report;
    private final Object stepByStep;
    private BufferManager bufferManager;
    public DeviceManager(Buffer buffer, ArrayList<Devices> devices, Object bufferNotEmptyNotifier, Report report, Object stepByStep, BufferManager bufferManager) {
        this.devices = devices;
        this.buffer = buffer;
        this.bufferNotEmptyNotifier = bufferNotEmptyNotifier;
        this.report = report;
        this.stepByStep = stepByStep;
        this.indexDevicesPlace = 0;
        this.bufferManager = bufferManager;
    }

    public int getIndexDevicesPlace() {
        return indexDevicesPlace;
    }

    private Devices getDevices() throws Exception {
        Devices device = null;
        synchronized (devices) {
            for (int i = indexDevicesPlace; i < devices.size(); i++) {
                if (!((device = devices.get(i)).isBusy())) {
                    if(i != devices.size()-1) {
                        indexDevicesPlace = i + 1;
                    } else {
                        indexDevicesPlace = 0;
                    }
                    return device;
                }
            }
            for (int i = 0; i < indexDevicesPlace; i++) {
                if (!((device = devices.get(i)).isBusy())) {
                    if(i != devices.size()-1) {
                        indexDevicesPlace = i + 1;
                    } else {
                        indexDevicesPlace = 0;
                    }
                    return device;
                }
            }
        }
        throw new Exception("Not free devices");
    }

    public List<Boolean> getDeviceStatuses() {
        List<Boolean> deviceStatuses = new ArrayList<>(devices.size());
        synchronized (devices) {
            for (Devices device : devices) {
                deviceStatuses.add(device.isBusy());
            }
        }
        return deviceStatuses;
    }

    private Request selectRequests() throws Exception {
        Request request = null;
        synchronized (buffer) {
            List<Request> requestList = buffer.buffer;
            int sizeRequestList = requestList.size();
            int numberRequest = -1;

            int indexOfMaxID = -1;
            for (int i = requestPointer; i < sizeRequestList; i++) {
                if(requestList.get(i) != null) {
                    requestPointer = i;
                    numberRequest = requestList.get(i).getIdRequest();
                    indexOfMaxID = i;
                    break;
                }
            }
            for (int i = 0; i < requestPointer; i++) {
                if (requestList.get(i)!= null && numberRequest <= requestList.get(i).getIdRequest()) {
                    requestPointer = i;
                    numberRequest = requestList.get(i).getIdRequest();
                    indexOfMaxID = i;
                }
            }
            if (indexOfMaxID != -1) {
                return buffer.get(indexOfMaxID);
            }
        }
        throw new Exception("Not have request");
    }

    public String printBuffer() {
        StringBuilder stringBuilder = new StringBuilder();
        synchronized (buffer.buffer) {
            int i = 0;
            for(Request request : buffer.buffer) {
                stringBuilder.append(bufferManager.getLastPlaceOfRequest() == i ? "*" : "" )
                        .append(request == null ? null : request.getIdRequest())
                        .append(" - ");
                ++i;
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (buffer.isEmpty()) {
                try {
                    synchronized (bufferNotEmptyNotifier) {
                        bufferNotEmptyNotifier.wait();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            synchronized (stepByStep) {
                try {
                    stepByStep.wait();
                } catch (InterruptedException ignored) {
                }
            }

            Devices device = null;
            try {
                device = getDevices();
            } catch (Exception e) {
                continue;
            }



            Request request = null;
            try {
                request = selectRequests();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                continue;
            }
            device.startWork(request);

        }
        long timeOfStopWorking = System.currentTimeMillis();
        for (Request i : buffer.buffer) {
            if(i != null) {
                report.increaseCanceledSourceRequestCount(i.getSourceNumber());
                report.increaseTimeRequestInBuffer(i.getSourceNumber(), timeOfStopWorking - i.getArrivalTimeInSystem());
                System.out.println("Request " + i.getIdRequest() + " canceled");
            }
        }
    }
}
