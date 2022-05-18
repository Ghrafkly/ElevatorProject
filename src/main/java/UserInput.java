import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class UserInput {
    private final Validator v = new Validator();
    private static final Logger LOGGER = LogManager.getLogger(UserInput.class);

    public boolean userInput(Thread commandGen, GenCommands genCommands) throws InterruptedException {
        boolean waitCheck = false;
        String input;
        Scanner scanner = new Scanner(System.in);
        do {
            input = scanner.nextLine();

            switch (v.valInput(input)) {
                case "command" -> {
                    for (String str : v.getCommands()) {
                        LOGGER.info(str);
                        Thread.sleep(1);
                    }
                    System.out.println("Command(s): " + v.getCommands());
                    v.setCommands(new ArrayList<>());
                }
                case "interval" -> {
                    int inp = Integer.parseInt(input);
                    if (!waitCheck && inp <= 0) {
                        commandGen.interrupt();
                        waitCheck = true;
                    } else if (waitCheck && inp > 0) {
                        commandGen = new Thread(genCommands, "commands");
                        commandGen.start();
                        waitCheck = false;
                    }
                    genCommands.setTimeInterval(inp);
                    System.out.printf("Changed time interval: %d\n", inp);
                    LOGGER.info(String.format("Changed time interval: %d", inp));
                }
                case "simulation" -> {
                    System.out.println("Simulation: " + input);
                }
                case "none" -> System.out.println("Error in user input");
            }
        } while (!Objects.equals(input, "stop"));

        return true;
    }
}
