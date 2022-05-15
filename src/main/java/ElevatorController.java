import java.util.ArrayList;

/*
    Rip out MAX_CAPACITY from creating the elevator.
    Put it into another class, such as scheduler.
 */

public class ElevatorController {
    private int maxFloor;
    private int minFloor;
    private int numberOfElevators;
    private int capacity;
    private final ArrayList<Elevator> elevators = new ArrayList<>();

    public ElevatorController() {
    }

    public void setMaxFloor(int maxFloor) {
        this.maxFloor = maxFloor;
    }

    public void setMinFloor(int minFloor) {
        this.minFloor = minFloor;
    }

    public void setNumberOfElevators(int numberOfElevators) {
        this.numberOfElevators = numberOfElevators;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setElevators() {
        for (int i = 0; i < numberOfElevators; i++)
            this.elevators.add(new Elevator(capacity, i));
    }

    public void runElevators() {
        elevators.forEach(Thread::start);
    }

    @Override
    public String toString() {
        return "ElevatorController[maxFloor=%d, minFloor=%d, numberOfElevators=%d, capacity=%d, elevators=%s]"
                .formatted(maxFloor, minFloor, numberOfElevators, capacity, elevators);
    }
}
