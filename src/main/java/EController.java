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

    /**
     * Initializer for Jackson usage
     */
    public EController() {
    }

    /**
     * Setter for maxFloor
     *
     * @param maxFloor      Takes in an integer
     */
    public void setMaxFloor(int maxFloor) {
        EController.maxFloor = maxFloor;
    }

    /**
     * Setter for minFloor
     *
     * @param minFloor      Takes in an integer
     */
    public void setMinFloor(int minFloor) {
        EController.minFloor = minFloor;
    }

    /**
     * Getter for maxFloor
     *
     * @return          Returns maxFloor
     */
    public int getMaxFloor() {
        return EController.maxFloor;
    }

    /**
     * Getter for minFloor
     *
     * @return          Returns minFloor
     */
    public int getMinFloor() {
        return EController.minFloor;
    }

    /**
     * Setter for capacity
     *
     * @param capacity      Takes in an integer
     */
    public void setCapacity(int capacity) {
        EController.capacity = capacity;
    }

    /**
     * Setter for number of elevators
     *
     * @param numberOfElevators         Takes in an integer
     */
    public void setNumberOfElevators(int numberOfElevators) {
        this.numberOfElevators = numberOfElevators;
    }

    /**
     * Getter for number of elevators
     *
     * @return          numberOFElevators
     */
    public int getNumberOfElevators() {
        return numberOfElevators;
    }

    /**
     * Creates the elevator threads
     */
    public void setElevatorThreads() {
        for (int i = 0; i < numberOfElevators; i++) {
            elevators.add(new Elevator(capacity, i));
        }
    }

    /**
     * Runs the elevator threads
     */
    public void runElevators() {
        for (int i = 0; i < elevators.size(); i++) {
            threads.add(new Thread(elevators.get(i), String.valueOf(i)));
        }
        threads.forEach(Thread::start);
    }

    /**
     * Getter for elevator array
     *
     * @return      An array of elevators. Size will be equal to the amount of elevator defined in the config file
     */
    public ArrayList<Elevator> getElevators() {
        return elevators;
    }
}
