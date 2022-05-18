import java.util.Objects;
import java.util.Scanner;

public class UserInput {
    private final Validator v = new Validator();

    public void userInput(Thread commandGen, GenCommands genCommands) throws InterruptedException {
        boolean waitCheck = false;
        String input;
        Scanner scanner = new Scanner(System.in);
        do {
            input = scanner.nextLine();

            switch (v.valInput(input)) {
                case "command" -> System.out.println("Command: " + input);
                case "interval" -> {
                    int inp = Integer.parseInt(input);
                    if (!waitCheck && inp <= 0) {
                        commandGen.interrupt();
                        waitCheck = true;
                    } else if (waitCheck && inp > 0) {
                        commandGen = new Thread(genCommands, "commands");
                        commandGen.start();
                        waitCheck = false;
                    }
                    System.out.printf("New time interval: %d\n", inp);
                    genCommands.setTimeInterval(inp);
                }
                case "simulation" -> System.out.println("Simulation: " + input);
                case "none" -> System.out.println("Error in user input");
            }
        } while (!Objects.equals(input, "stop"));

        commandGen.interrupt();
        Thread.sleep(1);
        System.out.println("Program Ended");
    }
}
