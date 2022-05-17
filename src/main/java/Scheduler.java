import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class Scheduler implements Runnable
{
    private ArrayList<Event> events;
    private final ArrayList<Elevator> elevators;
    private String inputs;
    private static final Logger LOGGER = LogManager.getLogger(Scheduler.class);

    public Scheduler(ArrayList<Elevator> elevators)
    {
        events = new ArrayList<>();
        this.elevators = elevators;
    }

    @Override
    public void run()
    {
        inputs = "1:2:3,4:5:2"; // TODO: dynamically get inputs
        LOGGER.info("test");

        while(true)
        {
            // Add inputs to the events arrayList
            if(!inputs.equals("")) {
                processInput(inputs);
            }
            inputs = "";

            // Loop through all the elevators and move it up or down depending on it's moveState
            for(Elevator elevator : elevators)
            {
                // Assign events to the appropriate elevator
                manageEventList(elevator, events, elevators);

                // Manage moveState based on the events assigned to each elevator
                manageMoveState(elevators);

                // Based on the move state, move the elevator up or down a floor
                switch(elevator.getMoveState())
                {
                    case DOWN:
                        moveFloor(elevator, EState.DOWN);
                        break;
                    case UP:
                        moveFloor(elevator, EState.UP);
                        break;
                    case IDLE:
                        break;
                }

                // If the elevator has reached the start or end of an event:
                ArrayList<Event> reachedStartEvent = reachStartOfEvent(elevator);
                ArrayList<Event> reachedEndEvent = reachEndOfEvent(elevator);

                // If the elevator has reached a source floor:
                if (!reachedStartEvent.isEmpty())
                {
                    updateElevatorCapacity(elevator, reachedStartEvent, true);
                }

                // If the elevator has reached a destination floor:
                if (!reachedEndEvent.isEmpty())
                {
                    updateElevatorCapacity(elevator, reachedEndEvent, false);
                }
            }
        }
    }

    /**
     * This function will update Elevator Capacity upon entering an SRC or DEST floor
     * @param elevator is the elevator being queried
     * @param events is the
     * @param add
     */
    public void updateElevatorCapacity(Elevator elevator, ArrayList<Event> events, boolean add)
    {
        for(Event event : events)
        {
            int currCapacity = elevator.getCurrentCapacity();

            // Update the number of occupants
            if(add)
            {
                elevator.setCurrentCapacity(currCapacity + event.getNumPeople());
            }
            else
            {
                elevator.setCurrentCapacity(currCapacity - event.getNumPeople());
            }
        }

        // Destination has been reached so job can be removed from the list
        if(!add)
        {
            cleanUpEvents(elevator.getEvents());
        }

        // Sleep the elevator to allow passengers to enter/leave
        if(events.size() > 0)
        {
            try {
                elevator.openOrCloseElevator();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Getter for events to be assigned
     * @return all events to be processed
     */
    public ArrayList<Event> getEvents()
    {
        return events;
    }

    /**
     * This function will convert inputs into Event objects and appended to Elevator's jobs
     * @param inputs in the form of src:dest:numPeople comma-separated
     */
    public void processInput(String inputs)
    {
        String[] inputsArr = inputs.split(",");
        for(String input : inputsArr)
        {
            Event event = createEvent(input);
            events.add(event);
        }
    }

    /**
     * This function will return an event that have started its journey.
     * @param elevator being queried
     * @return event with a src that matches the elevator's current floor otherwise null
     */
    public ArrayList<Event> reachStartOfEvent(Elevator elevator)
    {
        ArrayList<Event> events = new ArrayList<>();

        for(Event event : elevator.getEvents())
        {
            if(event.getSrc() == elevator.getCurrentFloor() && !event.getSrcReached())
            {
                event.setSrcReached(true);
                events.add(event);
            }
        }
        return events;
    }

    /**
     * This function will return an event that have completed its journey.
     * @param elevator being queried
     * @return event with a dest that matches the elevator's current floor otherwise null
     */
    public ArrayList<Event> reachEndOfEvent(Elevator elevator)
    {
        ArrayList<Event> events = new ArrayList<>();

        for(Event event : elevator.getEvents())
        {
            if(event.getDest() == elevator.getCurrentFloor() && event.getSrcReached())
            {
                event.setDelete(true);
                events.add(event);
            }
        }
        return events;
    }

    /**
     * This function takes a single String input in the form src:dest:numPeople and returns an Event object
     * @param input string in the form src:destination:numPeople
     * @return event object
     */
    public Event createEvent(String input)
    {
        int src = Integer.parseInt(input.split(":")[0]);
        int dest = Integer.parseInt(input.split(":")[1]);
        int numPeople = Integer.parseInt(input.split(":")[2]);

        return new Event(numPeople, src, dest);
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

    public void moveFloor(Elevator elevator, EState eState)
    {
        try
        {
            elevator.moveElevator();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(eState == EState.UP)
        {
            elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);
        }

        else if(eState == EState.DOWN)
        {
            elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);
        }
    }

    /**
     * Based on the events assigned to each elevator, we will change its moveState
     * @param elevators arraylist of elevators
     */
    public void manageMoveState(ArrayList<Elevator> elevators)
    {
        int minFloor = EController.minFloor;

        for(Elevator elevator : elevators)
        {
            // If the elevator is at the min floor and has no tasks, set it to IDLE
            if(elevator.getCurrentFloor() == minFloor && elevator.getEvents().size() == 0)
            {
                elevator.setMoveState(EState.IDLE);
            }

            // If the elevator is being called from a floor lower than the src floor to move down, need to handle it
            // If the elevator's state conflicts with the events' state then change it
            else if(elevator.getEvents().size() > 0)
            {
                Event event = elevator.getEvents().get(0);
                EState eState = getDirection(event);

                if(eState != elevator.getMoveState() && checkAllEventSrcReached(elevator))
                {
                    elevator.setMoveState(getDirection(elevator.getEvents().get(0)));
                }
            }


        }
    }

    public boolean checkAllEventSrcReached(Elevator elevator)
    {
        for(Event event : elevator.getEvents())
        {
            if (!event.getSrcReached())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * This will return the anticipated capacity of the elevator based on the Events assigned to it
     * @param elevator is the elevator being queried
     * @return the calculated capacity
     */
    public int getElevatorCurrCapacity(Elevator elevator)
    {
        int sum = 0;
        for(Event event : elevator.getEvents())
        {
            sum += event.getNumPeople();
        }

        return sum;
    }

    /**
     * This function will get all inputs that are travelling in the same direction as the elevator and can be reached
     * @param elevator is the object being queried
     * @param events are all the events that needs to be processed
     */
    public void manageEventList(Elevator elevator, ArrayList<Event> events, ArrayList<Elevator> elevators)
    {
        int currFloor = elevator.getCurrentFloor();
        int maxCapacity = elevator.MAX_CAPACITY;
        int minFloor = EController.minFloor;

        for (Event event : events)
        {
            int elevatorCapacity = getElevatorCurrCapacity(elevator);

            /*
             * Here we check if:
             * There are no available on route elevators, not available means:
             * 1. max capacity has been reached
             * 2. the floor is not on route
             * If 1 and 2 above are true then we try to use the idle elevators
             */
            if (!availableElevatorsOnRoute(elevators, event))
            {
                if(elevator.getMoveState() == EState.IDLE)
                {
                    // We need to tell the elevator to go up
                    elevator.setMoveState(EState.UP);
                    manageEvent(elevatorCapacity, maxCapacity, event, elevator);
                }
            }

            /*
             * Here we check if:
             * 1. the input is on route for the elevator
             * 2. the elevator has sufficient capacity
             */
            else if(elevator.getEvents().size() > 0)
            {
                EState direction = getDirection(elevator.getEvents().get(0));
                if (direction == EState.UP &&
                        currFloor < event.getSrc() &&
                        (elevatorCapacity != maxCapacity))
                {
                    manageEvent(elevatorCapacity, maxCapacity, event, elevator);

                }

                else if (direction == EState.DOWN &&
                        currFloor > event.getSrc() &&
                        (elevatorCapacity != maxCapacity))
                {
                    manageEvent(elevatorCapacity, maxCapacity, event, elevator);
                }
            }
        }

        /*
         * If the elevator:
         * 1. is above the min floor
         * 2. has no assigned task
         * Return to the ground
         */
        if (currFloor > minFloor && elevator.getEvents().size() == 0)
        {
            elevator.setMoveState(EState.DOWN);
        }

        cleanUpEvents(events);
    }

    /**
     * This function will assign a group of people into an elevator
     * @param currCapacity the current capacity of the elevator
     * @param maxCapacity the maximum capacity of the elevator
     * @param event the event being processed
     * @param elevator the elevator being considered for assignment
     */
    public void manageEvent(int currCapacity, int maxCapacity, Event event, Elevator elevator)
    {
        // Elevator to take as many as it can before exceeding the max capacity
        if(currCapacity + event.getNumPeople() > maxCapacity)
        {
            int numPeopleCanAdd = maxCapacity - currCapacity;
            Event elevatorEvent = new Event(numPeopleCanAdd, event.getSrc(), event.getDest());
            elevator.addEvent(elevatorEvent);
            event.setNumPeople(event.getNumPeople() - numPeopleCanAdd);
        }
        // Else the elevator can take all occupants
        else
        {
            elevator.addEvent(new Event(event.getNumPeople(), event.getSrc(), event.getDest()));
            event.setDelete(true);
        }
    }

    /**
     * This function will return true if there are elevators available on route for a given Event
     * @param elevators contains array of all elevators
     * @param event is the event being queried
     * @return true if there are elevators available for use, else false
     */
    public boolean availableElevatorsOnRoute(ArrayList<Elevator> elevators, Event event)
    {
        for(Elevator elevator : elevators)
        {
            EState currState = elevator.getMoveState();
            int currFloor = elevator.getCurrentFloor();
            int currCapacity = elevator.getCurrentCapacity();
            int maxCapacity = elevator.getMAX_CAPACITY();

            if (currState == EState.UP &&
                    currFloor < event.getSrc() &&
                    (currCapacity != maxCapacity))
            {
                return true;
            }

            else if (currState == EState.DOWN &&
                    currFloor > event.getSrc() &&
                    (currCapacity != maxCapacity))
            {
                return true;
            }
        }

        return false;
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
