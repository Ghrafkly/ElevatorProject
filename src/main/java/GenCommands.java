import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class GenCommands implements Runnable {
    public static String simulation = "normal";
    public static int floorLock = EController.minFloor;
    private static final Logger LOGGER = LogManager.getLogger(GenCommands.class);
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("ss.SSS");
    private final int cap = EController.capacity;
    private final int max = EController.maxFloor;
    private final int min = EController.minFloor;
    private String command;
    private String time;
    public int timeInterval;

    /**
     * Initializer for Jackson usage
     */
    public GenCommands() {
    }

    /**
     * Loop to generate commands. Is modified through the timeInterval and simulation variables
     *
     */
    public void generator() throws InterruptedException {
        while (true) {
            time = (LocalTime.now().format(format));
            int capacity = ThreadLocalRandom.current().nextInt(1, cap + 1);

            switch (simulation) {
                case "morning" -> setCommand(morningSim(capacity, floorLock));
                case "afternoon" -> setCommand(afternoonSim(capacity, floorLock));
                case "normal" -> setCommand(normalSim(capacity));
            }
            ;

            time = (LocalTime.now().format(format));
            LOGGER.info(time + " " + command);
            Thread.sleep(timeInterval);
        }
    }

    /**
     * Getter for commands generated
     *
     * @return      Time (seconds.MILLISECONDS) and the command for Scheduler processing
     */
    public String getCommand() {
        return String.format(time + " " + command);

    }

    /**
     * Simulation for a morning rush
     *
     * @param capacity      Randomly generated number of people, between 0 and MAX_CAPACITY inclusive
     * @param floorLock     Lets the user lock the source floor to a specific one
     * @return              Command generated
     */
    public String morningSim(int capacity, int floorLock) {
        int des = ThreadLocalRandom.current().nextInt(min, max + 1);
        while (des == floorLock)
            des = ThreadLocalRandom.current().nextInt(min, max + 1);

        return String.format("%d:%d:%d", floorLock, des, capacity);
    }

    /**
     * Simulation for afternoon rush
     *
     * @param capacity      Randomly generated number of people, between 0 and MAX_CAPACITY inclusive
     * @param floorLock     Lets the user lock the destination floor to a specific one
     * @return              Command generated
     */
    public String afternoonSim(int capacity, int floorLock) {
        int src = ThreadLocalRandom.current().nextInt(min, max + 1);
        while (floorLock == src)
            src = ThreadLocalRandom.current().nextInt(min, max + 1);

        return String.format("%d:%d:%d", src, floorLock, capacity);
    }

    /**
     * Normal simulation of a day. Random source and destination floors
     *
     * @param capacity      Randomly generated number of people, between 0 and MAX_CAPACITY inclusive
     * @return              Command generated
     */
    public String normalSim(int capacity) {
        int src = ThreadLocalRandom.current().nextInt(min, max + 1);
        int des = ThreadLocalRandom.current().nextInt(min, max + 1);
        while (des == src)
            des = ThreadLocalRandom.current().nextInt(min, max + 1);

        return String.format("%d:%d:%d", src, des, capacity);
    }

    /**
     * Setter for command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Setter for time interval
     *
     * @param timeInterval      Takes in an integer representing the time interval in milliseconds
     */
    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    /**
     * Override method for toString()
     *
     * @return      Custom format string
     */
    @Override
    public String toString() {
        return "GenCommands{" +
                "command='" + command + '\'' +
                ", timeInterval=" + timeInterval +
                '}';
    }

    /**
     * Override method for run()
     */
    @Override
    public void run() {
        try {
            generator();
        } catch (InterruptedException e) {
            System.out.println("Generator Command stopped");
        }
    }
}
