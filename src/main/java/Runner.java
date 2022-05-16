import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Runner {
    //Made protected to make tests work
    protected static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        File file = new File("config.json");
        Validator v = new Validator();

        Map<String, Integer> json = readFromJSONFile(file);
        String elevator = MAPPER.writeValueAsString(json.get("elevator"));
        String commands = MAPPER.writeValueAsString(json.get("commands"));

        EController elevatorController = MAPPER.readValue(elevator, EController.class);
        GenCommands genCommands = MAPPER.readValue(commands, GenCommands.class);

        if (v.validateConfig(elevatorController)) {
            elevatorController.setElevatorThreads();
            elevatorController.runElevators();

            genCommands.generator();
            System.out.println(genCommands.getCommand());
        }
        else {
            System.out.println("Please re-configure config.json and try again!");
        }
    }

    public static Map<String, Integer> readFromJSONFile(File source) throws IOException {
        return MAPPER.readValue(source, Map.class);
    }
}
