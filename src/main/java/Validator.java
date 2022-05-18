import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

public class Validator {
    private static final Logger logger = LogManager.getLogger();
    private final Pattern comP = Pattern.compile("^\\d+:\\d+:\\d+$");
    private final Pattern numeric = Pattern.compile("^\\d+$");
    private final Pattern alpha = Pattern.compile("^[a-zA-Z]+$");
    private ArrayList<String> commands = new ArrayList<>();

    public boolean valConfig() {
        return EController.maxFloor > EController.minFloor;
    }

    public String valInput(String input) {
        String str = "";
        if (input.contains(":")) {
            if (valCommand(input)) {
                str = "command";
            }
        } else if (numeric.matcher(input).find()) {
            if (valInterval(Integer.parseInt(input))) {
                str = "interval";
            }
        } else if (alpha.matcher(input).find()) {
            if (valSimulation(input)) {
                str = "simulation";
            }
        }
        return str;
    }

    public boolean valCommand(String command) {
        String[] input = command.split(",");
        for (String str : input)
            if (!comP.matcher(str).find())
                logger.error(String.format("Command (%s) of length: %d. Commands should have length 3 and format int:int:int.", str, input.length));
            else
                commands.add(str);

        return commands.size() > 0;
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

    public ArrayList<String> getCommands() {
        return commands;
    }

    public void setCommands(ArrayList<String> commands) {
        this.commands = commands;
    }
}
