import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Set;

public class Validator {
    private static final Logger logger = LogManager.getLogger();

    public boolean valConfig() {
        return EController.maxFloor > EController.minFloor;
    }

    public String valInput(String input) {
        String str = "";
        if (input.contains(":")) {
            if (valCommand(input))
                str = "command";
        } else if (isNumeric(input)) {
            if (valInterval(Integer.parseInt(input)))
                str = "interval";
        } else if (isAlpha(input)) {
            if (valSimulation(input))
                str = "simulation";
        }
        return str;
    }

    public boolean valCommand(String command) {
        String[] input = command.split(":");
        if (input.length != 3 || command.endsWith(":")) {
            logger.error(String.format("Command (%s) of length: %d. Commands should have length 3 and format int:int:int.", command, input.length));
            return false;
        }
        return true;
    }

    public boolean valInterval(int interval) {
        if (interval <= 100) {
            logger.error(String.format("Interval of %d is invalid. Please set the interval to a number higher than 100", interval));
            return false;
        }
        return true;
    }

    public boolean valSimulation(String sim) {
        Set<String> simulations = Set.of("morning", "afternoon", "normal");
        if (!simulations.contains(sim)) {
            logger.error(String.format("Incorrect (%s) simulation entered", sim));
            return false;
        }
        return true;
    }

    /**
     * Checks to see if the value in the string array is a numeric
     *
     * @param input         String of user input
     * @return boolean      If the string is numeric
     */
    public boolean isNumeric(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            logger.error("Command value not an integer!");
            return false;
        }
    }

    public boolean isAlpha(String str) {
        String[] check = str.split("");
        return Arrays.stream(check).noneMatch(this::isNumeric);
    }
}
