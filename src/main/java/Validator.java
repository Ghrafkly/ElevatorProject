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
//    private final Pattern alpha = Pattern.compile("^[a-zA-Z]+$");
    private ArrayList<String> commands = new ArrayList<>();
    private String time;

    public boolean valConfig() {
        return EController.maxFloor > EController.minFloor;
    }

    public String valInput(String input) throws InterruptedException {
        String str = "";
        input = input.stripTrailing();
        if (input.contains(":")) {
            if (valCommand(input)) {
                str = "command";
            }
        } else if (numeric.matcher(input).find()) {
            if (valInterval(Integer.parseInt(input))) {
                str = "interval";
            }
        } else if (valSimulation(input)) {
            str = "simulation";
        }
        return str;
    }

    public boolean valCommand(String command) throws InterruptedException {
        String[] input = command.split(",");
        for (String str : input)
            if (!comP.matcher(str).find())
                logger.error(String.format("Command (%s) of length: %d. Commands should have length 3 and format int:int:int.", str, input.length));
            else {
                String[] checkCommand = str.split(":");
                if (!Objects.equals(checkCommand[0], checkCommand[1])) {
                    String time = (LocalTime.now().format(format));
                    commands.add(time + " " + str);
                    Thread.sleep(1);
                } else {
                    System.out.println("Source and Destination are the same floor");
                    logger.error("Source and Destination are the same floor");
                }
            }
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
        String[] justSim = sim.split(" ");
        Set<String> simulations = Set.of("morning", "afternoon", "normal");

        for (int i = 0; i < justSim.length; i++){
            if (justSim[i].equals("normal") && justSim.length == 1){
                return true;
            }
            else if (!simulations.contains(justSim[i].toLowerCase())) {
                logger.error(String.format("Incorrect (%s) simulation entered", sim));
                return false;
            }
            else {
                return true;
            }
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
