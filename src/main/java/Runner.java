import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

public class Runner {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String FILE_NAME = "config.json";

    public static void main(String[] args) throws IOException {
        File file = new File(FILE_NAME);

        Map<String, Integer> json = readFromJSONFile(file);
        String elevator = MAPPER.writeValueAsString(json.get("elevator"));
        String commands = MAPPER.writeValueAsString(json.get("commands"));

        GenCommands genCommands = MAPPER.readValue(commands, GenCommands.class);
        ElevatorController elevatorController = MAPPER.readValue(elevator, ElevatorController.class);

        elevatorController.setElevatorThreads();
        elevatorController.runElevators();

        System.out.println(genCommands);
        System.out.println(elevatorController);
    }

    public static Map<String, Integer> readFromJSONFile(File source) throws IOException {
        Map<String, Integer> config;
            config = MAPPER.readValue(source, Map.class);
        return config;
    }
}
