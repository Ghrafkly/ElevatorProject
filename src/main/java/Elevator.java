public class Elevator {
    private final int TRAVEL_TIME_BETWEEN_FLOORS = 1000;
    private final int TIME_TO_OPEN_AND_CLOSE_DOOR = 1000;

    private final int MAX_CAPACITY;
    private int nextFloor = 0;
    private int moveState = 0;
    private int elevatorID;
    private int currentFloor = 0;
    private int currentCapacity = 0;
    private boolean availability = true;

    public Elevator(int MAX_CAPACITY, int elevatorID) {
        this.MAX_CAPACITY = MAX_CAPACITY;
        this.elevatorID = elevatorID;
    }
}
