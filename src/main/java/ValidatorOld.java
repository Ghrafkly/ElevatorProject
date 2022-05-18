import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Checks commands and user input to ensure correct commands are sent to the scheduler
 */
public class ValidatorOld implements IValidator {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Validates the input in the config file
     *
     * @param eController
     * @return boolean
     */
    public boolean validateConfig(EController eController) {
        return EController.maxFloor >= EController.minFloor;
    }

    /**
     * Check to see if the user command is valid
     *
     * @param command
     * @return boolean
     */
    @Override
    public boolean validate(String command) {
        String[] input = command.split(":");

        if (input.length != 3 || command.lastIndexOf(":") == command.length() - 1) {
            logger.error(String.format("Command (%s) of length: %d. Commands should have length 3 and format int:int:int.", command, input.length));
            return false;
        } else {
            for (String s : input)
                if (!isNumeric(s)) {
                    logger.error(String.format("Command (%s) is not numeric. Command should be int:int:int.", command));
                    return false;
                }
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
