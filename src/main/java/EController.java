import java.util.ArrayList;

/*
    Rip out MAX_CAPACITY from creating the elevator.
    Put it into another class, such as scheduler.
 */

public class EController {
    public static int maxFloor;
    public static int minFloor;
    public static int capacity;
    private int numberOfElevators;
    private final ArrayList<Thread> elevators = new ArrayList<>();

    public EController() {
    }

    public void setMaxFloor(int maxFloor) {
        EController.maxFloor = maxFloor;
    }

    public void setMinFloor(int minFloor) {
        EController.minFloor = minFloor;
    }

    public void setCapacity(int capacity) {
        EController.capacity = capacity;
    }

    public void setNumberOfElevators(int numberOfElevators) {
        this.numberOfElevators = numberOfElevators;
    }

    // Need to link the Elevator object to a thread somehow
    public void setElevatorThreads() {
        ERunnable runnable = new ERunnable();
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
