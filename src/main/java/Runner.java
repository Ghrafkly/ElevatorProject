import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Runner {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String FILE_NAME = "config.json";

    public static void main(String[] args) throws IOException {
        File file = new File(FILE_NAME);

        ElevatorController elevatorController = MAPPER.readValue(file, ElevatorController.class);
        elevatorController.setElevatorThreads();
        elevatorController.runElevators();
    }
}
