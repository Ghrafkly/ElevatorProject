import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.OptionalInt;

public class Elevator implements Runnable, FrameGUI {
    private final int TRAVEL_TIME_BETWEEN_FLOORS = 1000;
    private final int TIME_TO_OPEN_AND_CLOSE_DOOR = 1000;

    public final int MAX_CAPACITY;
    private final int ELEVATOR_ID;
    private int nextFloor = 0;
    private EState moveState = EState.IDLE;
    private int currentFloor = 0;
    private int currentCapacity = 0;
    private static final Logger LOGGER = LogManager.getLogger(Elevator.class);
    private ArrayList<Event> events;
    private ArrayList<Event> receivedEvents;
    private int numSrcReached = 0;
    private int predictedCapacity = 0;

    public Elevator(int MAX_CAPACITY, int elevatorID) {
        this.MAX_CAPACITY = MAX_CAPACITY;
        this.ELEVATOR_ID = elevatorID;
        events = new ArrayList<>();
        receivedEvents = new ArrayList<>();
    }

    @Override
    public synchronized int getCurrentFloor() {
        return currentFloor;
    }

    @Override
    public synchronized String getCommand() {
        String returnStr = "";
        for(Event event : events)
        {
            returnStr += String.format("(%d:%d:%d) ", event.getSrc(), event.getDest(), event.getNumPeople());
        }

        return returnStr;
    }

    @Override
    public synchronized int getPeople() {
        return currentCapacity;
    }

    @Override
    public synchronized EState getState() {
        return moveState;
    }

    public synchronized int getELEVATOR_ID() {
        return ELEVATOR_ID;
    }

    public void setMoveState(EState moveState) {
        this.moveState = moveState;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public synchronized ArrayList<Event> getEvents() {
        return events;
    }

    public synchronized void addEvent(Event event) {
        receivedEvents.add(event);
    }

    public synchronized ArrayList<Event> getSchedulerEvents() {
        return receivedEvents;
    }

    public int getPredictedCapacity() {
        return predictedCapacity;
    }

    public void openOrCloseElevator() throws InterruptedException {
        moveState = EState.STOP;
        Thread.sleep(TIME_TO_OPEN_AND_CLOSE_DOOR);
    }

    public void moveElevator() throws InterruptedException {
        Thread.sleep(TRAVEL_TIME_BETWEEN_FLOORS);
    }

    @Override
    public String toString() {
        return "Elevator{" +
                "MAX_CAPACITY=" + MAX_CAPACITY +
                ", nextFloor=" + nextFloor +
                ", moveState=" + moveState +
                ", elevatorID=" + ELEVATOR_ID +
                '}';
    }

    @Override
    public void run() {
        while (true) {
            if(receivedEvents.size() > 0)
            {
                predictedCapacity += receivedEvents.stream().mapToInt(event -> event.getNumPeople()).sum();
                events.addAll(receivedEvents);
                receivedEvents.clear();
            }

            updateElevatorCapacity();

            // Manage moveState based on the events assigned to each elevator
            manageMoveState();

            // Based on the move state, move the elevator up or down a floor
            switch (getState())
            {
                case DOWN:
                    moveFloor(EState.DOWN);
                    break;
                case UP:
                    moveFloor(EState.UP);
                    break;
            }
        }
    }

    /**
     * This function will update Elevator Capacity upon entering an SRC or DEST floor
     */
    public void updateElevatorCapacity()
    {
        ArrayList<Event> eventsToRemove = new ArrayList<>();

        for(Event event : events)
        {
            if(event.getSrc() == currentFloor && !event.getSrcReached())
            {
                event.setSrcReached(true);
                setCurrentCapacity(currentCapacity + event.getNumPeople());
                try {
                    openOrCloseElevator();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else if(event.getDest() == currentFloor && event.getSrcReached())
            {
                predictedCapacity -= event.getNumPeople();
                setCurrentCapacity(currentCapacity - event.getNumPeople());
                eventsToRemove.add(event);
                try {
                    openOrCloseElevator();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        events.removeAll(eventsToRemove);

    }

    public boolean reachedAllSource(ArrayList<Event> events)
    {
        for(Event event : events)
        {
            if(!event.getSrcReached())
            {
                return false;
            }
        }
        return true;
    }

    public void moveFloor(EState eState)
    {
        // .info(String.format("Moving ID: %d CurrFloor %d EventsSize %d", getELEVATOR_ID(), currentFloor, events.size()));
        try
        {
            moveElevator();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(eState == EState.UP && (currentFloor + 1) <= EController.maxFloor)
        {
            setCurrentFloor(currentFloor + 1);
        }

        else if(eState == EState.DOWN && (currentFloor - 1)  >= EController.minFloor)
        {
            setCurrentFloor(currentFloor - 1);
        }
    }

    /**
     * Based on the events assigned to each elevator, we will change its moveState
     */
    public void manageMoveState()
    {
        int minFloor = EController.minFloor;
        int eventsSize = events.size();

        if(getCurrentFloor() == EController.maxFloor)
        {
            setMoveState(EState.DOWN);
        }

        // If the elevator is currently idle and has been allocated a task then move it up
        else if(moveState == EState.IDLE && eventsSize > 0)
        {
            setMoveState(EState.UP);
        }

        // If the elevator is at the min floor and has no tasks, set it to IDLE
        else if(getCurrentFloor() == minFloor && eventsSize == 0)
        {
            setMoveState(EState.IDLE);
        }

        // If the elevator has completed all its task and is currently above the min Floor then move down
        else if (getCurrentFloor() > minFloor && eventsSize == 0)
        {
            setMoveState(EState.DOWN);
        }

        // This deals with having multiple events allocated to it
        else if(eventsSize > 0)
        {
            Event event = getEvents().get(0);
            EState eState = getDirection(event);
            OptionalInt maxDest = getEvents().stream().mapToInt(x -> event.getDest()).max();
            boolean reachedAllSource = reachedAllSource(events);

            // If it's an up elevator
            if(eState == EState.UP &&
                    currentFloor != maxDest.getAsInt())
            {
                setMoveState(eState.UP);
            }

            // If it's a down elevator
            else if(eState == EState.DOWN &&
                    reachedAllSource)
            {
                setMoveState(eState.DOWN);
            }

            else if(eState == EState.DOWN &&
                    !reachedAllSource &&
                    currentFloor < event.getSrc())
            {
                setMoveState(eState.UP);
            }

            else if(eState == EState.DOWN &&
                    currentFloor > event.getSrc())
            {
                setMoveState(eState.DOWN);
            }
        }

    }

    /**
     * @return the direction the elevator needs to travel in
     */
    public EState getDirection(Event event)
    {
        if(event.getDest() > event.getSrc())
        {
            return EState.UP;
        }
        return EState.DOWN;
    }
}
