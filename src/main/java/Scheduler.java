import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class Scheduler implements Runnable
{
    private final ArrayList<Elevator> elevators;
    private String input;
    private String tempString;
    private static final Logger LOGGER = LogManager.getLogger(Scheduler.class);
    private ArrayList<Event> schedulerEvents;
    private GenCommands genCommands;

    public Scheduler(ArrayList<Elevator> elevators, ArrayList<Event> eControllerEvents, GenCommands genCommands)
    {
        this.elevators = elevators;
        this.schedulerEvents = new ArrayList<>();
        this.genCommands = genCommands;
        input = "";
        tempString = "";
    }

    @Override
    public void run()
    {
        while(true)
        {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tempString = genCommands.getCommand();

            if(!tempString.equals(input) && !tempString.isBlank())
            {
                input = tempString;
                processInput(input);

                // Loop through all the elevators and move it up or down depending on it's moveState

                for (Elevator elevator : elevators) {
                    // Assign events to the appropriate elevator
                    manageEventList(elevator, schedulerEvents);
                }

            }
        }
    }

    /**
     * This function will convert inputs into Event objects and appended to Elevator's jobs
     * @param input in the form of src:dest:numPeople comma-separated
     */
    public void processInput(String input)
    {
        String[] genCommand = input.split(" ");
        String[] inputsArr = genCommand[1].split(",");
        for(String str : inputsArr)
        {
            Event event = createEvent(str);
            schedulerEvents.add(event);
        }
    }

    /**
     * This function takes a single String input in the form src:dest:numPeople and returns an Event object
     *
     * @param input string in the form src:destination:numPeople
     * @return event object
     */
    public Event createEvent(String input)
    {
        int src = Integer.parseInt(input.split(":")[0]);
        int dest = Integer.parseInt(input.split(":")[1]);
        int numPeople = Integer.parseInt(input.split(":")[2]);

        return new Event(numPeople, src, dest, -1); // -1 elevatorID means it's not assigned yet
    }

    /**
     * @return the direction the elevator needs to travel in
     */
    public EState getDirection(Event event)
    {
        return event.getDest() > event.getSrc()
                ? EState.UP
                : EState.DOWN;
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
     *
     * @param elevator is the object being queried
     * @param events are all the events that needs to be processed
     */
    public void manageEventList(Elevator elevator, ArrayList<Event> events)
    {
        int maxCapacity = elevator.MAX_CAPACITY;

        for (Event event : events)
        {
            int elevatorCapacity = getElevatorCurrCapacity(elevator);
            int currFloor = elevator.getCurrentFloor();
            int eventSrc = event.getSrc();
            int eventDest = event.getDest();
            EState eventDir = getDirection(event);

            if(elevator.getEvents().size() == 0 &&
                    elevator.getState() == EState.DOWN &&
                    eventDir == EState.DOWN &&
                    currFloor > event.getSrc())
            {
                manageEvent(elevatorCapacity, maxCapacity, event, elevator);
            }

            /*
             * Here we check if:
             * 1. the input is on route for the elevator
             * 2. the elevator has sufficient capacity
             */
            else if(elevator.getEvents().size() > 0)
            {
                EState direction = getDirection(elevator.getEvents().get(0));
                int maxDest = elevator.getEvents().get(0).getDest();

                if (direction == EState.UP &&
                        getDirection(event) == EState.UP &&
                        currFloor < eventSrc &&
                        (elevatorCapacity != maxCapacity))
                {
                    manageEvent(elevatorCapacity, maxCapacity, event, elevator);
                }

                else if (direction == EState.DOWN &&
                        eventDir == EState.UP &&
                        elevator.getState() == EState.UP &&
                        currFloor < eventSrc &&
                        eventDest < maxDest &&
                        (elevatorCapacity != maxCapacity))
                {
                    manageEvent(elevatorCapacity, maxCapacity, event, elevator);
                }
            }

            else if (elevator.getState() == EState.IDLE)
            {
                manageEvent(elevatorCapacity, maxCapacity, event, elevator);
            }
        }

        // If even in schedulerEvents has been fully processed then delete it
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
        //LOGGER.info(String.format("Elevator ID: %d, src:%d dest:%d numPeople:%d", elevator.getELEVATOR_ID(), event.getSrc(), event.getDest(), event.getNumPeople()));
        event.setElevatorId(elevator.getELEVATOR_ID());

        // Elevator to take as many as it can before exceeding the max capacity
        if(currCapacity + event.getNumPeople() > maxCapacity)
        {
            int numPeopleCanAdd = maxCapacity - currCapacity;
            Event elevatorEvent = new Event(numPeopleCanAdd, event.getSrc(), event.getDest(), elevator.getELEVATOR_ID());
            elevator.addEvent(elevatorEvent);
            event.setNumPeople(event.getNumPeople() - numPeopleCanAdd);
        }
        // Else the elevator can take all occupants
        else
        {
            elevator.addEvent(new Event(event.getNumPeople(), event.getSrc(), event.getDest(), elevator.getELEVATOR_ID()));
            event.setDelete(true);
        }

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
                it.remove();
        }
    }
}
