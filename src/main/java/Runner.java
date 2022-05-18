import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Runner {
    protected static final ObjectMapper MAPPER = new ObjectMapper();
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
        Scheduler scheduler = new Scheduler(eController.getElevators());
        Thread schedulerThread = new Thread(scheduler);

        FrameView fm = new FrameView(eController.getMinFloor(), eController.getMaxFloor(), eController.getNumberOfElevators(), eController.getElevators());
        Thread graphics = new Thread(fm);
        graphics.start();

        commandGen.start();

        eController.setElevatorThreads();
        eController.runElevators();
        schedulerThread.start();

        UserInput u = new UserInput();
        u.userInput(commandGen, genCommands);
        fm.close();
    }

    public Map<String, Integer> readFromJSONFile(File source) throws IOException {
        return MAPPER.readValue(source, Map.class);
    }
}
