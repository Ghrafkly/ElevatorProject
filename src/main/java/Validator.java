import java.util.ArrayList;

public class Validator implements IValidator {

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

            return false;

        }
    }

    /**
     * Check to see if the user command is valid
     * @param command
     * @return boolean
     */
    @Override
    public boolean validate(String command) {

        try {

            if (command.lastIndexOf(":") == command.length() - 1)
                return false;

            String[] input = command.split(":");

            if (input.length != 3)
                return false;

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
