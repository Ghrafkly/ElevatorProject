import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Checks commands and user input to ensure correct commands are sent to the scheduler
 */
public class Validator implements IValidator {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Checks to see if the value in the string array is an integer
     * @param input
     * @return boolean
     */
    private boolean isInt(String input) {
        try {

            Integer.parseInt(input);
            return true;

        } catch (NumberFormatException e) {

            logger.error("Command value not an integer!");
            return false;

        }
    }

    /**
     * Validates the input in the config file
     * @param eController
     * @return boolean
     */
    public boolean validateConfig(EController eController){

        return eController.getMaxFloor() >= eController.getMinFloor();

    }

    /**
     * Check to see if the user command is valid
     * @param command
     * @return boolean
     */
    @Override
    public boolean validate(String command) {

        try {

            if (command.lastIndexOf(":") == command.length() - 1) {
                logger.error("String ending in ':' is not a valid command");
                return false;
            }

            String[] input = command.split(":");

            if (input.length != 3){
                logger.error("Command of length: " + input.length + ". Commands should have length 3.");
                return false;
            }


            for (String s : input) {

                boolean check = isInt(s);

                if (!check) {

                    return false;

                }
            }

            return true;

        } catch (Exception e) {

            return false;

        }
    }
}
