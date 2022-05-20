import com.fasterxml.jackson.annotation.JsonIgnore;
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
     * Stop elevators
     */
    public void stopElevators() {
        elevators.forEach(e -> e.setAlive(false));
    }

    /**
     * Getter for elevator array
     *
     * @return      An array of elevators. Size will be equal to the amount of elevator defined in the config file
     */
    public ArrayList<Elevator> getElevators() {
        return elevators;
    }


    /**
     * For testing purposes
     *
     * @return      Array of thread
     */
    @JsonIgnore
    public ArrayList<Thread> getThreads() {
        return threads;
    }

//    SAVING THE STATE OF THE ELEVATORS ATTEMPT
//    -------------------------------------------------------
//    public void serialise() throws IOException {
//        try {
//            for (Elevator e : elevators) {
//                MAPPER.writeValue(new File(String.format("elevatorState%d.json",e.getELEVATOR_ID())), e);
//            }
//        } catch (IOException e) {
//            System.out.println("Serialisation failed");
//        }
//    }
//
//    /**
//     * Maps the json file to String Integer pairs
//     *
//     * @param source            File name
//     * @return                  Map
//     */
//    public Map<String, Object> readFromJSONFile(File source) throws IOException {
//        return MAPPER.readValue(source, Map.class);
//    }
//
//    /**
//     * Deserializes json file if it is the same as the temp one
//     *
//     * @param i         Elevator ID
//     * @return          Elevator
//     */
//    public Elevator deserialize(int i) {
//        try {
//            File file = new File(String.format("elevatorState%d.json",i));
//            File file1 = new File("config.json");
//            File file2 = new File("configTemp.json");
//            TypeReference<HashMap<String, Object>> type = new TypeReference<>() {};
//
//            Map<String, Object> json = readFromJSONFile(file);
//            Map<String, Object> json1 = readFromJSONFile(file1);
//            Map<String, Object> json2 = readFromJSONFile(file2);
//
//            MapDifference<String, Object> difference = Maps.difference(json1, json2);
//
//            if (difference.toString().equals("equal")) {
//                String elevator = MAPPER.writeValueAsString(json);
//                System.out.println(elevator);
//                Elevator e = MAPPER.readValue(elevator, Elevator.class);
//                System.out.println(e);
//                return e;
//            } else {
//                Path one = Path.of("config.json");
//                Path two = Path.of("configTemp.json");
//
//                Files.copy(one, two, StandardCopyOption.REPLACE_EXISTING);
//                return new Elevator(capacity, i);
//            }
//        } catch (IOException e) {
//            System.out.println("Deserialization failed");
//        }
//        return new Elevator(capacity, i);
//    }
}
