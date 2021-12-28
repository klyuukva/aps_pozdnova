public class Request {
    private int idRequest;
    private int sourceNumber;
    private int age;
    private int allergy;
    private int spentTime;
    private int brains;
    private int cost;
    private String answer;
    private long arrivalTimeInSystem;

    public int getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(int idRequest) {
        this.idRequest = idRequest;
    }

    public int getAge() {
        return age;
    }

    public int getSourceNumber() {
        return sourceNumber;
    }

    public void setSourceNumber(int sourceNumber) {
        this.sourceNumber = sourceNumber;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAllergy() {
        return allergy;
    }

    public void setAllergy(int allergy) {
        this.allergy = allergy;
    }

    public int getSpentTime() {
        return spentTime;
    }

    public void setSpentTime(int spentTime) {
        this.spentTime = spentTime;
    }

    public int getBrains() {
        return brains;
    }

    public void setBrains(int brains) {
        this.brains = brains;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setArrivalTimeInSystem(long time) {
        this.arrivalTimeInSystem = time;
    }

    public long getArrivalTimeInSystem() {
        return this.arrivalTimeInSystem;
    }

    @Override
    public String toString() {
        return "Request{" +
                "idRequest=" + idRequest +
                ", age=" + age +
                ", allergy=" + allergy +
                ", spentTime=" + spentTime +
                ", brains=" + brains +
                ", cost=" + cost +
                ", answer='" + answer +
                ", arrivalTimeInSystem='" + arrivalTimeInSystem + '\'' +
                '}';
    }
}
