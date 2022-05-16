/**
 * Interface to make the FrameView graphics work with your elevator if using calls the the elevator thread
 * , not message passing.
 * You must at least fully implement getCurrentFloor, the rest are optional for
 * full implementation (you need to declare them in elevator , but, if not using
 * them they don't need to do anything more.
 * @author Donald Witcombe
 * @version	1.01
 *
 */
public interface FrameGUI {
    int getCurrentFloor();        // Get the current floor number

    String getCommand();        // Get the current command being used

    int getPeople();            // Get the number of people in the elevator

    EState getState();            // Get the current elevator state for colour change
}
