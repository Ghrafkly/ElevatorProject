public class GenCommands {
    private String command;
    private int timeInterval;

    public GenCommands() {
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
