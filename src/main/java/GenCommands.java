import java.util.concurrent.ThreadLocalRandom;

public class GenCommands {
    private String command;
    private int timeInterval;
    private final int cap = EController.capacity;
    private final int max = EController.maxFloor;
    private final int min = EController.minFloor;
    public GenCommands() {
    }

    public void generator() {
        int capacity = ThreadLocalRandom.current().nextInt(1,cap+1);
        int src = ThreadLocalRandom.current().nextInt(min,max+1);
        int des = ThreadLocalRandom.current().nextInt(min,max+1);

        setCommand(String.format("%d:%d:%d",src,des,capacity));
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
}
