import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Runner {
    //Made protected to make tests work
    protected static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger logger = LogManager.getLogger();


    public static void main(String[] args) throws IOException {
        File file = new File("config.json");
        Validator v = new Validator();

        Map<String, Integer> json = readFromJSONFile(file);
        String elevator = MAPPER.writeValueAsString(json.get("elevator"));
        String commands = MAPPER.writeValueAsString(json.get("commands"));

        EController elevatorController = MAPPER.readValue(elevator, EController.class);
        GenCommands genCommands = MAPPER.readValue(commands, GenCommands.class);

        try{
            genCommands.generator();
            v.validate(genCommands.getCommand());

            if (v.validateConfig(elevatorController)) {
                elevatorController.setElevatorThreads();
                elevatorController.runElevators();
            }
            else {
                logger.info("Please re-configure config.json and try again!");
            }
        }
        catch (IllegalArgumentException iae){
            logger.error("Please re-configure config.json and try again!");
        }

    }

    public static Map<String, Integer> readFromJSONFile(File source) throws IOException {
        return MAPPER.readValue(source, Map.class);
    }
}
