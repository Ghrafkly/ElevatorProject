import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.OptionalInt;

public class Elevator implements Runnable, FrameGUI {
    private final int TRAVEL_TIME_BETWEEN_FLOORS = 1000;
    private final int TIME_TO_OPEN_AND_CLOSE_DOOR = 1000;
    public final int MAX_CAPACITY;
    private final int ELEVATOR_ID;
    private static final Logger LOGGER = LogManager.getLogger(Elevator.class);
    private EState moveState;
    private int currentFloor;
    private int currentCapacity;                // Number of people currently in the elevator
    private ArrayList<Event> events;            // Holds events allocated to elevator
    private ArrayList<Event> receivedEvents;    // Holds events received by the scheduler
    private int predictedCapacity;              // Number of people allocated to an elevator to be transported

    /**
     * The default constructor for Elevator
     *
     * @param MAX_CAPACITY the maximum capacity of the elevator
     * @param elevatorID   unique identifier for an elevator
     */
    public Elevator(int MAX_CAPACITY, int elevatorID) {
        this.MAX_CAPACITY = MAX_CAPACITY;
        this.ELEVATOR_ID = elevatorID;
        events = new ArrayList<>();
        receivedEvents = new ArrayList<>();
        moveState = EState.IDLE;
        currentFloor = EController.minFloor;
        currentCapacity = 0;
        predictedCapacity = 0;
    }

    /**
     * Implementation of getCurrentFloor from FrameGUI
     *
     * @return the current floor of the elevator
     */
    @Override
    public synchronized int getCurrentFloor() {
        return currentFloor;
    }

    /**
     * Implementation of getCommand from FrameGUI
     *
     * @return the jobs worked on by the elevator in the form (src : dest : numPeople)
     */
    @Override
    public String getCommand() {
        String returnStr = "";
        for (Event event : events) {
            returnStr += String.format("(%d:%d:%d) ", event.getSrc(), event.getDest(), event.getNumPeople());
        }
        return returnStr;
    }

    /**
     * Implementation of getPeople from FrameGUI
     *
     * @return the number of people inside the elevator
     */
    @Override
    public synchronized int getPeople() {
        return currentCapacity;
    }

    /**
     * Implementation of getState from FrameGUI
     *
     * @return the state of the elevator, will be UP, DOWN, IDLE or STOP
     */
    @Override
    public synchronized EState getState() {
        return moveState;
    }

    /**
     * Getter for the ELEVATOR_ID
     *
     * @return
     */
    public synchronized int getELEVATOR_ID() {
        return ELEVATOR_ID;
    }

    /**
     * Setter for the moveState
     *
     * @param moveState
     */
    public void setMoveState(EState moveState) {
        this.moveState = moveState;
    }

    /**
     * Setter for the current floor
     *
     * @param currentFloor
     */
    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    /**
     * Setter for current capacity
     *
     * @param currentCapacity
     */
    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    /**
     * Getter for elevator's allocated jobs by the scheduler
     *
     * @return
     */
    public synchronized ArrayList<Event> getEvents() {
        return events;
    }

    /**
     * Adds an event to the receivedEvents ArrayList which contains jobs to be added to Elevator's job list
     *
     * @param event
     */
    public synchronized void addEvent(Event event) {
        receivedEvents.add(event);
    }

    /**
     * Getter for schedulerEvents which contains all the events allocated by the scheduler
     *
     * @return
     */
    public synchronized ArrayList<Event> getSchedulerEvents() {
        return receivedEvents;
    }

    /**
     * Getter for the number of people assigned to an elevator for delivery
     *
     * @return
     */
    public synchronized int getPredictedCapacity() {
        return predictedCapacity;
    }

    /**
     * This will sleep the elevator when it open or closes
     *
     * @throws InterruptedException
     */
    public void openOrCloseElevator() throws InterruptedException {
        moveState = EState.STOP;
        Thread.sleep(TIME_TO_OPEN_AND_CLOSE_DOOR);
    }

    /**
     * This will sleep the elevator when it moves between a floor
     *
     * @throws InterruptedException
     */
    public void moveElevator() throws InterruptedException {
        Thread.sleep(TRAVEL_TIME_BETWEEN_FLOORS);
    }

    public void setPredictedCapacity(int predictedCapacity) {
        this.predictedCapacity = predictedCapacity;
    }

    @Override
    public String toString() {
        return "Elevator{" +
                "MAX_CAPACITY=" + MAX_CAPACITY +
                ", moveState=" + moveState +
                ", elevatorID=" + ELEVATOR_ID +
                '}';
    }

    /**
     * Implementation of Runnable
     */
    @Override
    public void run() {
        // Main loop
        while (true) {
            // Batch process all events that have been allocated by the scheduler
            if (receivedEvents.size() > 0) {
                predictedCapacity += receivedEvents.stream().mapToInt(event -> event.getNumPeople()).sum();
                events.addAll(receivedEvents);
                receivedEvents.clear();
            }

            // Update the elevator capacity
            updateElevatorCapacity();

            // Manage moveState based on the events assigned to each elevator
            manageMoveState();

            // Based on the move state, move the elevator up or down a floor
            switch (getState()) {
                case DOWN -> moveFloor(EState.DOWN);
                case UP -> moveFloor(EState.UP);
            }
        }
    }

    /**
     * This function will update Elevator Capacity upon entering an SRC or DEST floor
     */
    public void updateElevatorCapacity() {
        // ArrayList containing events that need to be removed when it completes its journey
        ArrayList<Event> eventsToRemove = new ArrayList<>();
        boolean doorsOpen = false; // Door will open upon entering a source or destination floor

        for (Event event : events) {
            // If the elevator has reached the start of a journey for a job
            if (event.getSrc() == currentFloor && !event.getSrcReached()) {
                // Update the capacity of the elevator
                event.setSrcReached(true);
                doorsOpen = true;
                setCurrentCapacity(currentCapacity + event.getNumPeople());
            }

            // If the elevator has completed its journey for a job
            else if (event.getDest() == currentFloor && event.getSrcReached()) {
                // Update the capacity of the elevator and remove the job as its completed
                predictedCapacity -= event.getNumPeople();
                doorsOpen = true;
                setCurrentCapacity(currentCapacity - event.getNumPeople());
                eventsToRemove.add(event);

            }
        }

        if (doorsOpen) {
            try {
                openOrCloseElevator();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Remove all events that are needed to be deleted
        events.removeAll(eventsToRemove);
    }

    /**
     * This will move the Floor by using the parameter eState
     *
     * @param eState either UP or DOWN
     */
    public void moveFloor(EState eState) {
        try {
            moveElevator();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (eState == EState.UP && (currentFloor + 1) <= EController.maxFloor) {
            setCurrentFloor(currentFloor + 1);
        } else if (eState == EState.DOWN && (currentFloor - 1) >= EController.minFloor) {
            setCurrentFloor(currentFloor - 1);
        }
    }

    /**
     * Based on the events assigned to each elevator, we will adjust its moveState.
     */
    public void manageMoveState() {
        int minFloor = EController.minFloor;
        int eventsSize = events.size();

        // If the elevator has reached the TOP floor then move it down as there's nowhere else to go.
        if (getCurrentFloor() == EController.maxFloor) {
            setMoveState(EState.DOWN);
        }

        // If the elevator is currently idle and has been allocated a task then move it up.
        else if (moveState == EState.IDLE && eventsSize > 0) {
            setMoveState(EState.UP);
        }

        // If the elevator is at the min floor and has no tasks, set it to IDLE.
        else if (getCurrentFloor() == minFloor && eventsSize == 0) {
            setMoveState(EState.IDLE);
        }

        // If the elevator has completed all its task and is currently above the min Floor then move down.
        else if (getCurrentFloor() > minFloor && eventsSize == 0) {
            setMoveState(EState.DOWN);
        }

        // Dealing with multiple events:
        else if (eventsSize > 0) {
            /*
             * An elevator will either be a UP or DOWN elevator.
             * UP elevator means its responsible for getting events that go UP.
             * DOWN elevator means its responsible for getting events that go DOWN.
             * A DOWN elevator will try to grab UP events only if it's along the way.
             */

            Event event = getEvents().get(0); // To determine if it's a UP or DOWN elevator we look at the first event allocated.
            EState eState = getDirection(event);
            OptionalInt maxDest = getEvents().stream().mapToInt(x -> x.getDest()).max(); // The maximum destination floor among its jobs allocated.

            // If it's an up elevator and hasn't reached the maximum destination floor among its jobs then keep going up
            if (eState == EState.UP &&
                    currentFloor != maxDest.getAsInt()) {
                setMoveState(EState.UP);
            } else if (moveState == EState.DOWN) {
                setMoveState(EState.DOWN);
            }

            // If it's a down elevator and has reached the source of the down even then go down
            else if (eState == EState.DOWN &&
                    event.getSrcReached()) {
                setMoveState(EState.DOWN);
            }
        }

    }

    /**
     * This function will determine the direction of the Event, either UP or DOWN
     *
     * @return an EState either UP or DOWN
     */
    public EState getDirection(Event event) {
        if (event.getDest() > event.getSrc()) {
            return EState.UP;
        }
        return EState.DOWN;
    }
}
