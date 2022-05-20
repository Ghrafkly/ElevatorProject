import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Scanner;

public class UserInput {
    private final Validator v = new Validator();
    private static final Logger LOGGER = LogManager.getLogger(UserInput.class);
    private String userInput = "";

    /**
     * Process user input
     *
     * @param commandGen                Takes in a thread
     * @param genCommands               Takes in an object
     * @return                          True if "stop" is entered
     */
    public boolean userInput(Thread commandGen, GenCommands genCommands) throws InterruptedException {
        boolean waitCheck = false;
        String input;
        Scanner scanner = new Scanner(System.in);
        do {
            input = scanner.nextLine();

            String s = v.valInput(input);
            switch (s) {
                case "command" -> {
                    for (String str : v.getCommands()) {
                        userInput = str;
                        LOGGER.info(str);
                        Thread.sleep(500);
                    }
                    System.out.println("Command(s): " + v.getCommands());
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
                    String[] justSim = input.split(" ");
                    switch (justSim[0].toLowerCase()) {
                        case "morning", "afternoon", "normal" -> {
                            if (justSim.length > 1) {
                                if (Integer.parseInt(justSim[1]) < EController.minFloor || Integer.parseInt(justSim[1]) > EController.maxFloor) {
                                    System.out.printf("Floor %s is out of range of floors defined in the config file MinFloor: %d MaxFloor: %d\n"
                                            , justSim[1], EController.minFloor, EController.maxFloor);
                                } else {
                                    System.out.println("Simulation: " + justSim[0]);
                                    GenCommands.simulation = justSim[0];
                                    GenCommands.floorLock = Integer.parseInt(justSim[1]);
                                }
                            } else {
                                System.out.println("Simulation: " + justSim[0]);
                                GenCommands.simulation = justSim[0];
                                GenCommands.floorLock = EController.minFloor;
                            }
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + input);
                    }
                }
                case "halt" -> {
                    waitCheck = true;
                    commandGen.interrupt();
                }
                case "stop" -> {
                    return true;
                }
                case "", "none" -> System.out.println("Error in user input");
            }
        } while (!Objects.equals(input, "stop"));

        return true;
    }

    /**
     * Getter for user input
     *
     * @return          User Input
     */
    public String getUserInput() {
        return userInput;
    }
}
