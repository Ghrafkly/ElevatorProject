public class Elevator {
    private final int TRAVEL_TIME_BETWEEN_FLOORS = 1000;
    private final int TIME_TO_OPEN_AND_CLOSE_DOOR = 1000;

    public final int MAX_CAPACITY;
    private final int ELEVATOR_ID;
    private int nextFloor = 0;
    private EState moveState = EState.IDLE;
    private int currentFloor = 0;
    private int currentCapacity = 0;
    private boolean availability = true;

    public Elevator(int MAX_CAPACITY, int elevatorID) {
        this.MAX_CAPACITY = MAX_CAPACITY;
        this.ELEVATOR_ID = elevatorID;
    }

    public int getMAX_CAPACITY() {
        return MAX_CAPACITY;
    }

    public int getNextFloor() {
        return nextFloor;
    }

    public EState getMoveState() {
        return moveState;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public boolean isAvailability() {
        return availability;
    }

    public int getELEVATOR_ID() {
        return ELEVATOR_ID;
    }

    public void setNextFloor(int nextFloor) {
        this.nextFloor = nextFloor;
    }

    public void setMoveState(EState moveState) {
        this.moveState = moveState;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    @Override
    public String toString() {
        return "Elevator{" +
                "MAX_CAPACITY=" + MAX_CAPACITY +
                ", nextFloor=" + nextFloor +
                ", moveState=" + moveState +
                ", elevatorID=" + ELEVATOR_ID +
                '}';
    }
}
