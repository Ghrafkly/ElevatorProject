import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Runner {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        File file = new File("config.json");

        Map<String, Integer> json = readFromJSONFile(file);
        String elevator = MAPPER.writeValueAsString(json.get("elevator"));
        String commands = MAPPER.writeValueAsString(json.get("commands"));

        EController elevatorController = MAPPER.readValue(elevator, EController.class);
        GenCommands genCommands = MAPPER.readValue(commands, GenCommands.class);

        elevatorController.setElevatorThreads();
        elevatorController.runElevators();

        genCommands.generator();
        System.out.println(genCommands.getCommand());
    }

    public static Map<String, Integer> readFromJSONFile(File source) throws IOException {
        return MAPPER.readValue(source, Map.class);
    }
}
