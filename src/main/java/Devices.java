public class Devices implements Runnable {
    //private int idDevices;
    private boolean isBusy;
    private long processingTime;
    private Request request;
    private static int count;
    private final int number;
    private final Object newRequestNotifier;
    private Report report;
    private final Object stepByStep;

    public Devices(Report report, Object stepByStep) {
        this.number = count++;
        long processingTime = (long) ((Math.random() * (Constants.MAX_SPEED - Constants.MIN_SPEED)) + Constants.MIN_SPEED);
        this.processingTime = processingTime;
        newRequestNotifier = new Object();
        this.report = report;
        this.stepByStep = stepByStep;
    }


    public int getNumber() {
        return number;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }


    public void startWork(Request request) {
        synchronized (stepByStep) {
            try {
                stepByStep.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.request = request;
        synchronized (newRequestNotifier) {
            isBusy = true;
            newRequestNotifier.notify();
        }
    }

    public void getAnimal(Request request) {
        int ballSum = request.getAge() + request.getAllergy() + request.getBrains() + request.getCost() + request.getSpentTime();

        if (ballSum >= 9) {
            request.setAnswer("Собека");
        }
        if (ballSum == 7 || ballSum == 8) {
            request.setAnswer("Кiт");
        }
        if (ballSum == 5 || ballSum == 6) {
            request.setAnswer("Крыска");
        }
        if (ballSum == 3 || ballSum == 4) {
            request.setAnswer("Рыбонька");
        }
        if (ballSum == 1 || ballSum == 2) {
            request.setAnswer("Жабка");
        }
        if (ballSum == 0) {
            request.setAnswer("Вам, кажется, не нужно животное...");
        }
    }

    @Override
    public void run() {
        long startBusytime = 0;
        long startDowntime = 0;
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (newRequestNotifier) {
                try {
                    startDowntime = System.currentTimeMillis();
                    newRequestNotifier.wait();
                    startBusytime = System.currentTimeMillis();
                    report.increaseDowntimeInDevice(number, startBusytime - startDowntime);
                    System.out.println("Device " + number + " start");
                } catch (InterruptedException e) {
                    report.increaseDowntimeInDevice(number, System.currentTimeMillis() - startDowntime);
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            try {
                Thread.sleep((long) (Math.random() * (Constants.MAX_SPEED - Constants.MIN_SPEED)) + Constants.MIN_SPEED);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                report.increaseCanceledSourceRequestCount(request.getSourceNumber());
                report.increaseTimeRequestInBuffer(request.getSourceNumber(), startBusytime - request.getArrivalTimeInSystem());
                e.printStackTrace();
                break;
            }
            try {
                synchronized (new Object()) {
                    getAnimal(request);
                    System.out.println(request);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            report.increaseProcessedSourceRequestCount(request.getSourceNumber());
            report.increaseBusytimeInDevice(number, System.currentTimeMillis() - startBusytime);
            report.increaseTimeOfWorkWithRequest(request.getSourceNumber(), System.currentTimeMillis() - request.getArrivalTimeInSystem());
            System.out.println("Device " + number + ": finish process request " + request.getIdRequest());
            isBusy = false;
        }
    }
}
