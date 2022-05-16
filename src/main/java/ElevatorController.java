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
    private final ArrayList<Thread> elevators = new ArrayList<>();

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

    // Need to link the Elevator object to a thread somehow
    public void setElevatorThreads() {
        ElevatorRunnable runnable = new ElevatorRunnable();
        for (int i = 0; i < numberOfElevators; i++)
            elevators.add(new Thread(runnable, String.valueOf(i)));
    }

    public void runElevators() {
        elevators.forEach(Thread::start);
    }

    @Override
    public String toString() {
        return "ElevatorController{" +
                "maxFloor=" + maxFloor +
                ", minFloor=" + minFloor +
                ", numberOfElevators=" + numberOfElevators +
                ", capacity=" + capacity +
                ", elevators=" + elevators +
                '}';
    }
}
