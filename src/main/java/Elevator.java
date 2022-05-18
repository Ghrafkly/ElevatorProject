import java.util.ArrayList;
import java.util.Iterator;

public class Elevator implements Runnable {
    private final int TRAVEL_TIME_BETWEEN_FLOORS = 1000;
    private final int TIME_TO_OPEN_AND_CLOSE_DOOR = 1000;

    public final int MAX_CAPACITY;
    private final int ELEVATOR_ID;
    private int nextFloor = 0;
    private EState moveState = EState.IDLE;
    private int currentFloor = 0;
    private int currentCapacity = 0;

    private FrameView frameView;
    private boolean availability = true;
    private ArrayList<Event> events;

    public Elevator(int MAX_CAPACITY, int elevatorID, FrameView frameView) {
        this.MAX_CAPACITY = MAX_CAPACITY;
        this.ELEVATOR_ID = elevatorID;
        events = new ArrayList<>();
        this.frameView = frameView;
    }

    // Use and remove commands
    public int getMAX_CAPACITY() {
        return MAX_CAPACITY;
    }

    public int getNextFloor() {
        return nextFloor;
    }

    public EState getMoveState() {
        return moveState;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public boolean isAvailability() {
        return availability;
    }

    public int getELEVATOR_ID() {
        return ELEVATOR_ID;
    }

    public void setNextFloor(int nextFloor) {
        this.nextFloor = nextFloor;
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

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void openOrCloseElevator() throws InterruptedException {
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
        Thread graphics = new Thread(frameView);
        graphics.start();
        while (true) {
            if (events.size() > 0) {
                // Manage moveState based on the events assigned to each elevator
                manageMoveState();

                // Based on the move state, move the elevator up or down a floor
                switch (getMoveState()) {
                    case DOWN:
                        moveFloor(EState.DOWN);
                        break;
                    case UP:
                        moveFloor(EState.UP);
                        break;
                    case IDLE:
                        break;
                }

                // If the elevator has reached the start or end of an event:
                ArrayList<Event> reachedStartEvent = reachStartOfEvent();
                ArrayList<Event> reachedEndEvent = reachEndOfEvent();

                // If the elevator has reached a source floor:
                if (!reachedStartEvent.isEmpty()) {
                    updateElevatorCapacity(reachedStartEvent, true);
                }

                // If the elevator has reached a destination floor:
                if (!reachedEndEvent.isEmpty()) {
                    updateElevatorCapacity(reachedEndEvent, false);
                }
            }
        }
    }
    /**
     * This function will update Elevator Capacity upon entering an SRC or DEST floor
     * @param events is the
     * @param add
     */
    public void updateElevatorCapacity(ArrayList<Event> events, boolean add)
    {
        for(Event event : events)
        {
            int currCapacity = getCurrentCapacity();

            // Update the number of occupants
            if(add)
            {
                setCurrentCapacity(currCapacity + event.getNumPeople());
            }
            else
            {
                setCurrentCapacity(currCapacity - event.getNumPeople());
            }
        }

        // Destination has been reached so job can be removed from the list
        if(!add)
        {
            cleanUpEvents(getEvents());
        }

        // Sleep the elevator to allow passengers to enter/leave
        if(events.size() > 0)
        {
            try {
                openOrCloseElevator();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * This function will return an event that have started its journey.
     * @return event with a src that matches the elevator's current floor otherwise null
     */
    public ArrayList<Event> reachStartOfEvent()
    {
        ArrayList<Event> events = new ArrayList<>();

        for(Event event : getEvents())
        {
            if(event.getSrc() == getCurrentFloor() && !event.getSrcReached())
            {
                event.setSrcReached(true);
                events.add(event);
            }
        }
        return events;
    }

    /**
     * This function will return an event that have completed its journey.
     * @return event with a dest that matches the elevator's current floor otherwise null
     */
    public ArrayList<Event> reachEndOfEvent()
    {
        ArrayList<Event> events = new ArrayList<>();

        for(Event event : getEvents())
        {
            if(event.getDest() == getCurrentFloor() && event.getSrcReached())
            {
                event.setDelete(true);
                events.add(event);
            }
        }
        return events;
    }

    public void moveFloor(EState eState)
    {
        try
        {
            moveElevator();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(eState == EState.UP)
        {
            setCurrentFloor(getCurrentFloor() + 1);
        }

        else if(eState == EState.DOWN)
        {
            setCurrentFloor(getCurrentFloor() - 1);
        }
    }

    /**
     * Based on the events assigned to each elevator, we will change its moveState
     */
    public void manageMoveState()
    {
        int minFloor = EController.minFloor;

        // If the elevator is at the min floor and has no tasks, set it to IDLE
        if(getCurrentFloor() == minFloor && getEvents().size() == 0)
        {
            setMoveState(EState.IDLE);
        }

        // If the elevator is currently idle and has been allocated a task then move it up
        else if(getMoveState() == EState.IDLE && getEvents().size() > 0)
        {
            setMoveState(EState.UP);
        }

        // This deals with having multiple down events
        else if(getEvents().size() > 0)
        {
            Event event = getEvents().get(0);
            EState eState = getDirection(event);

            if(eState != getMoveState() && checkAllEventSrcReached())
            {
                setMoveState(getDirection(getEvents().get(0)));
            }
        }

        // If the elevator has completed all its task and is currently above the min Floor then move down
        else if (getCurrentFloor() > minFloor && getEvents().size() == 0)
        {
            setMoveState(EState.DOWN);
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

    public boolean checkAllEventSrcReached()
    {
        for(Event event : getEvents())
        {
            if (!event.getSrcReached())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * This function will remove any events that have the boolean 'remove' turned on
     * @param events are events needing to be cleaned
     */
    public void cleanUpEvents(ArrayList<Event> events)
    {
        for(Iterator<Event> it = events.iterator(); it.hasNext();)
        {
            if (it.next().getDelete())
            {
                it.remove();
            }
        }
    }
}
