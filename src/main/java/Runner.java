import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Runner {
    protected static final ObjectMapper MAPPER = new ObjectMapper();
    private EController eController;
    private GenCommands genCommands;

    /**
     * Main method for the program
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        Runner r = new Runner();
        Validator v = new Validator();
        File file = new File("config.json");

        r.createObjects(file);

        if (v.valConfig())
            r.startThreads();
        else
            System.out.println("Issue with config file");
    }

    /**
     * Creates objects needs for the program such as the EController and GenCommands
     *
     * @param file                  Takes in a file name
     */
    public void createObjects(File file) throws IOException {
        Map<String, Integer> json = readFromJSONFile(file);

        String elevator = MAPPER.writeValueAsString(json.get("elevator"));
        String commands = MAPPER.writeValueAsString(json.get("commands"));

        eController = MAPPER.readValue(elevator, EController.class);
        genCommands = MAPPER.readValue(commands, GenCommands.class);
    }

    /**
     * Creates, Starts and Stops threads
     */
    public void startThreads() throws InterruptedException {
        Thread commandGen = new Thread(genCommands, "commands");

        eController.setElevatorThreads();
        eController.runElevators();
        FrameView fm = new FrameView(eController.getMinFloor(), eController.getMaxFloor(), eController.getNumberOfElevators(), eController.getElevators());
        Thread graphics = new Thread(fm);


        UserInput u = new UserInput();
        Scheduler scheduler = new Scheduler(eController.getElevators(), genCommands, u);
        Thread schedulerThread = new Thread(scheduler);

        commandGen.start();
        graphics.start();
        schedulerThread.start();

        if (u.userInput(commandGen, genCommands)) {
            commandGen.interrupt();
            fm.close();
            Thread.sleep(1);
            System.out.println("Program Ended");
        }
    }

    /**
     * Maps the json file to String Integer pairs
     *
     * @param source            File name
     * @return                  Map
     */
    public Map<String, Integer> readFromJSONFile(File source) throws IOException {
        return MAPPER.readValue(source, Map.class);
    }
}