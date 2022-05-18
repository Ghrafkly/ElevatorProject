import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Runner {
    protected static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Logger logger = LogManager.getLogger();

    private EController eController;
    private GenCommands genCommands;

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

    public void createObjects(File file) throws IOException {
        Map<String, Integer> json = readFromJSONFile(file);

        String elevator = MAPPER.writeValueAsString(json.get("elevator"));
        String commands = MAPPER.writeValueAsString(json.get("commands"));

        eController = MAPPER.readValue(elevator, EController.class);
        genCommands = MAPPER.readValue(commands, GenCommands.class);
    }

    public void startThreads() throws InterruptedException {
        Thread commandGen = new Thread(genCommands, "commands");
        Scheduler scheduler = new Scheduler(eController.getElevators(), eController.getEcontrollerEvents(), genCommands);
        Thread schedulerThread = new Thread(scheduler);
        Thread controllerThread = new Thread(eController);

        FrameView fm = new FrameView(eController.getMinFloor(), eController.getMaxFloor(), eController.getNumberOfElevators(), eController.getElevators());


        Validator v = new Validator();
        commandGen.start();
        controllerThread.start();

        genCommands.generator();
        v.valCommand(genCommands.getCommand());

        logger.info("This was printed");

        eController.setElevatorThreads(fm);
        eController.runElevators();
        schedulerThread.start();


        UserInput u = new UserInput();
        u.userInput(commandGen, genCommands);
        fm.close();
    }

        public Map<String, Integer> readFromJSONFile (File source) throws IOException {
            return MAPPER.readValue(source, Map.class);
        }
}

