import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadLocalRandom;

public class GenCommands implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(GenCommands.class);
    private String command;
    public int timeInterval;
    private final int cap = EController.capacity;
    private final int max = EController.maxFloor;
    private final int min = EController.minFloor;
    public GenCommands() {
    }

    // SRC and DES cannot be the same
    public void generator() throws InterruptedException {
        while (true) {
            int capacity = ThreadLocalRandom.current().nextInt(1, cap + 1);
            int src = ThreadLocalRandom.current().nextInt(min, max + 1);
            int des = ThreadLocalRandom.current().nextInt(min, max + 1);
            while (des == src)
                des = ThreadLocalRandom.current().nextInt(min, max + 1);
            setCommand(String.format("%d:%d:%d", src, des, capacity));
            LOGGER.info(command);
            Thread.sleep(timeInterval);
        }
    }

    public String getCommand() {
        return command;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    @Override
    public String toString() {
        return "GenCommands{" +
                "command='" + command + '\'' +
                ", timeInterval=" + timeInterval +
                '}';
    }

    @Override
    public void run() {
        try {
            generator();
        } catch (InterruptedException e) {
            System.out.println("Generator Command stopped");
        }
    }
}
