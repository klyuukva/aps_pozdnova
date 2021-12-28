import java.util.*;

public class Buffer {
    private int MAX_BUFFER_SIZE = Constants.MAX_BUFFER_SIZE;
    private Report report;
    final Vector<Request> buffer;
    private volatile int counter;
    private final Object bufferNotEmptyNotifier;

    public Buffer (Object bufferNotEmptyNotifier, Report report) {
        this.bufferNotEmptyNotifier = bufferNotEmptyNotifier;
        buffer = new Vector<>(Collections.nCopies(MAX_BUFFER_SIZE, null));
        this.report = report;
    }

    public Request get(int i) {
        Request request = buffer.get(i);
        buffer.set(i, null);
        counter--;
        return request;
    }
    public  int getOldestRequest() {
        int minID = -1;
        int i = 0;
        int minIndex = 0;
        for (i = 0; i < buffer.size(); i++) {
            if(buffer.get(i) != null) {
                minID = buffer.get(i).getIdRequest();
                minIndex = i;
                break;
            }
        }
        for (; i < buffer.size(); i++) {
            if(minID > buffer.get(i).getIdRequest()) {
                minID = buffer.get(i).getIdRequest();
                minIndex = i;
            }
        }
        return minIndex;
    }

    public void push(int i, Request request) {
        Request oldRequest = buffer.get(i);


        if (oldRequest == null) {
            buffer.set(i, request);
            ++counter;
            synchronized (bufferNotEmptyNotifier) {
                bufferNotEmptyNotifier.notify();
            }
        } else {
            buffer.set(getOldestRequest(), request);
            report.increaseTimeRequestInBuffer(oldRequest.getSourceNumber(), System.currentTimeMillis() - oldRequest.getArrivalTimeInSystem());
            System.out.println("Request " + oldRequest.getIdRequest() + " canceled");
            report.increaseCanceledSourceRequestCount(oldRequest.getSourceNumber());
        }
    }
    public synchronized boolean isEmpty() {
        return counter == 0;
    }



}
