import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class Runner {
    //Made protected to make tests work
    protected static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws IOException, InterruptedException {
        File file = new File("config.json");
        Validator v = new Validator();

        Map<String, Integer> json = readFromJSONFile(file);
        String elevator = MAPPER.writeValueAsString(json.get("elevator"));
        String commands = MAPPER.writeValueAsString(json.get("commands"));

        EController elevatorController = MAPPER.readValue(elevator, EController.class);
        GenCommands genCommands = MAPPER.readValue(commands, GenCommands.class);
        Thread commandGen = new Thread(genCommands, "commands");

        elevatorController.setElevatorThreads();
        elevatorController.runElevators();
        commandGen.start();

        boolean waitCheck = false;
        String input;
        Scanner scanner = new Scanner(System.in);
        do {
            /*
                Time Interval: Integer
                Command: src:dest:ppl
                Simulate: morning, afternoon
             */
            input = scanner.nextLine();
            if (isNumeric(input)) {
                 int inp = Integer.parseInt(input);

                if (!waitCheck && inp <= 0) {
                    commandGen.interrupt();
                    waitCheck = true;
                } else if (waitCheck && inp > 0) {
                    commandGen = new Thread(genCommands, "commands");
                    commandGen.start();
                    waitCheck = false;
                }

                System.out.printf("New time interval: %d\n", inp);
                genCommands.setTimeInterval(inp);
            } else if (isAlpha(input) && !input.equals("stop")) {
                System.out.println("Simulation: " + input);
            } else if (v.validate(input)) {
                System.out.println("Command: " + input);
            }
        } while (!Objects.equals(input, "stop"));

        commandGen.interrupt();
        Thread.sleep(1);
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

    public static boolean isAlpha(String str) {
        String[] check = str.split("");
        return Arrays.stream(check).noneMatch(Runner::isNumeric);
    }

    public static Map<String, Integer> readFromJSONFile(File source) throws IOException {
        return MAPPER.readValue(source, Map.class);
    }
}
