import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class EController {
    public static int maxFloor;
    public static int minFloor;
    public static int capacity;
    private int numberOfElevators;
    private final ArrayList<Elevator> elevators = new ArrayList<>();
    private final ArrayList<Thread> threads = new ArrayList<>();
    private static final Logger LOGGER = LogManager.getLogger(EController.class);

    public EController() {
    }

    public void setMaxFloor(int maxFloor) {
        EController.maxFloor = maxFloor;
    }

    public void setMinFloor(int minFloor) {
        EController.minFloor = minFloor;
    }

    public int getMaxFloor() {
        return EController.maxFloor;
    }

    public int getMinFloor() {
        return EController.minFloor;
    }

    public void setCapacity(int capacity) {
        EController.capacity = capacity;
    }

    public void setNumberOfElevators(int numberOfElevators) {
        this.numberOfElevators = numberOfElevators;
    }

    public int getNumberOfElevators() {
        return numberOfElevators;
    }

    // Need to link the Elevator object to a thread somehow
    public void setElevatorThreads() {
        for (int i = 0; i < numberOfElevators; i++) {
            elevators.add(new Elevator(capacity, i));
        }
    }

    public void runElevators() {
        for (int i = 0; i < elevators.size(); i++) {
            threads.add(new Thread(elevators.get(i), String.valueOf(i)));
        }
        threads.forEach(Thread::start);
    }

    public ArrayList<Elevator> getElevators() {
        return elevators;
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
