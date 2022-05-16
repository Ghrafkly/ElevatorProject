import java.util.ArrayList;

public class Validator implements IValidator {

    /**
     * Checks to see if the value in the string array is an integer
     * @param input
     * @return boolean
     */
    protected boolean isInt(String input) {
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

            String first = command.substring(0, command.indexOf(":"));
            command = command.substring(command.indexOf(":") + 1);
            String second = command.substring(0, command.indexOf(":"));
            command = command.substring(command.indexOf(":") + 1);
            String third = command;


            String[] input = {first, second, third};

            for (int i = 0; i < input.length; i++) {

                boolean check = isInt(input[i]);

                if (check == false) {

                    return false;

                }
            }

            return true;

        } catch (Exception e) {

            return false;

        }
    }
}
