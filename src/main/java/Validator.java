import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Set;
import java.util.*;
import java.util.regex.Pattern;

public class Validator {
    private static final Logger logger = LogManager.getLogger();
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("ss.SSS");
    private final Pattern comP = Pattern.compile("^\\d+:\\d+:\\d+$");
    private final Pattern numeric = Pattern.compile("^\\d+$");
    private ArrayList<String> commands = new ArrayList<>();

    /**
     * Validates the config file
     *
     * @return      Returns a boolean based on the validity of the config file
     */
    public boolean valConfig() {
        return EController.maxFloor > EController.minFloor;
    }

    /**
     * Validates input
     *
     * @param input        Takes in user input
     * @return             Type of input: command, interval, simulation
     */
    public String valInput(String input) throws InterruptedException {
        String str = "none";
        input = input.stripTrailing();
        if (input.contains(":")) {
            if (valCommand(input)) {
                str = "command";
            }
        } else if (numeric.matcher(input).find()) {
            str = "interval";
        } else if (valSimulation(input)) {
            str = "simulation";
        } else {
            return valOther(input);
        }
        return str;
    }

    /**
     * Validates command
     *
     * @param command           Takes in a user entered command
     * @return                  Boolean
     */
    public boolean valCommand(String command) throws InterruptedException {
        boolean fail = false;
        String message = "";
        String[] input = command.split(",");
        for (String str : input)
            if (!comP.matcher(str).find())
                logger.error(String.format("Command (%s) of length: %d. Commands should have length 3 and format int:int:int.", str, input.length));
            else {
                String[] checkCommand = str.split(":");
                int[] test = Arrays.stream(checkCommand).mapToInt(Integer::parseInt).toArray();
                if (test[0] == test[1]) {
                    message = "Source and Destination are the same floor";
                    fail = true;
                } else if (test[0] < EController.minFloor || test[1] < EController.minFloor) {
                    message = "Floor entered is less than allowed floor";
                    fail = true;
                } else if (test[0] > EController.maxFloor || test[1] > EController.maxFloor) {
                    message = "Floor entered is greater than allowed floor";
                    fail = true;
                }

                if (fail) {
                    System.out.println(message);
                    logger.error(message);
                } else {
                    String time = (LocalTime.now().format(format));
                    commands.add(time + " " + str);
                    Thread.sleep(1);
                }
            }
        return commands.size() > 0;
    }

    /**
     * Validates simulation entered
     *
     * @param sim           Takes in a user inputted interval
     * @return              Boolean
     */
    public boolean valSimulation(String sim) {
        String[] justSim = sim.split(" ");
        Set<String> simulations = Set.of("morning", "afternoon", "normal");
        if (!simulations.contains(justSim[0].toLowerCase())) {
            logger.error(String.format("Incorrect (%s) simulation entered", sim));
            return false;
        }
        return true;
    }

    /**
     * Accepts other inputs. Handling for halting the program or stopping it outright
     *
     * @param other         User input
     * @return              Input if valid, otherwise returns "none"
     */
    public String valOther(String other) {
        return switch (other) {
            case "stop", "halt" -> other;
            default -> "none";
        };
    }

    /**
     * Getter for commands
     *
     * @return      An Array of commands
     */
    public ArrayList<String> getCommands() {
        return commands;
    }

    /**
     * For the purposes of testing
     *
     * @param commands          Takes in an array of commands
     */
    public void setCommands(ArrayList<String> commands) {
        this.commands = commands;
    }
}
