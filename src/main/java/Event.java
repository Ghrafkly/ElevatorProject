public class Event {
    private int numPeople;
    private int src;
    private int dest;
    private boolean delete;
    private boolean srcReached;
    private int elevatorId;

    public Event(int numPeople, int src, int dest, int elevatorId) {
        this.numPeople = numPeople;
        this.src = src;
        this.dest = dest;
        this.delete = false;
        this.srcReached = false;
        this.elevatorId = elevatorId;
    }

    public int getNumPeople() {
        return numPeople;
    }

    public int getSrc() {
        return src;
    }

    public int getDest() {
        return dest;
    }

    public boolean getDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public void setNumPeople(int numPeople) {
        this.numPeople = numPeople;
    }

    public void setSrcReached(boolean srcReached) {
        this.srcReached = srcReached;
    }

    public boolean getSrcReached() {
        return srcReached;
    }

    public int getElevatorId() {
        return elevatorId;
    }

    public void setElevatorId(int elevatorId)
    {
        this.elevatorId = elevatorId;
    }
}
