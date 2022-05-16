import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class Runner {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) throws IOException, InterruptedException {
        File file = new File("config.json");

        Map<String, Integer> json = readFromJSONFile(file);
        String elevator = MAPPER.writeValueAsString(json.get("elevator"));
        String commands = MAPPER.writeValueAsString(json.get("commands"));

        EController elevatorController = MAPPER.readValue(elevator, EController.class);
        GenCommands genCommands = MAPPER.readValue(commands, GenCommands.class);
        Thread commandGen = new Thread(genCommands, "commands");
        commandGen.start();

        elevatorController.setElevatorThreads();
        elevatorController.runElevators();

        String input;
        Scanner scanner = new Scanner(System.in);
        do {
            input = scanner.nextLine();
            if (isNumeric(input)) {
                System.out.println(input);
                genCommands.setTimeInterval(Integer.parseInt(input));
            } else {
                // Parse command elsewhere
            }
        } while (!Objects.equals(input, "stop"));
        commandGen.interrupt();
        Thread.sleep(1000);
        System.out.println("Program Ended");
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Map<String, Integer> readFromJSONFile(File source) throws IOException {
        return MAPPER.readValue(source, Map.class);
    }
}
