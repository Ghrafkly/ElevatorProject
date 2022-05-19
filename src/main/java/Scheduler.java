import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class Scheduler implements Runnable
{
    private final ArrayList<Elevator> elevators;
    private String genCommandInput;
    private String userInput;
    private String tempGenCommandInput;
    private String tempUserInput;
    private static final Logger LOGGER = LogManager.getLogger(Scheduler.class);
    private ArrayList<Event> schedulerEvents;
    private GenCommands genCommands;
    private UserInput userInputObj;

    public Scheduler(ArrayList<Elevator> elevators, GenCommands genCommands, UserInput userInputObj)
    {
        this.elevators = elevators;
        this.schedulerEvents = new ArrayList<>();
        this.genCommands = genCommands;
        this.userInputObj = userInputObj;
        genCommandInput = "";
        userInput = "";
        tempUserInput = "";
        tempGenCommandInput = "";
    }

    @Override
    public void run()
    {
        while(true)
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tempGenCommandInput = genCommands.getCommand();
            tempUserInput = userInputObj.getUserInput();

            if(!tempGenCommandInput.equals(genCommandInput) && !tempGenCommandInput.isBlank())
            {
                genCommandInput = tempGenCommandInput;
                processGenCommandInput(genCommandInput);

                // Loop through all the elevators and move it up or down depending on it's moveState

                for (Elevator elevator : elevators) {
                    // Assign events to the appropriate elevator
                    manageEventList(elevator, schedulerEvents);
                }
            }

            if(!tempUserInput.equals(userInput) && !tempUserInput.isBlank())
            {
                userInput = tempUserInput;
                processUserInput(userInput);

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
    public void processGenCommandInput(String input)
    {
        String[] genCommand = input.split(" ");
        String[] inputsArr = genCommand[1].split(",");
        for(String str : inputsArr)
        {
            Event event = createEvent(str);
            schedulerEvents.add(event);
        }
    }

    public void processUserInput(String userInput)
    {
        LOGGER.info("triggered");
        String[] genCommand = userInput.split(" ");
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
            int elevatorCapacity = elevator.getPredictedCapacity();
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

            else if (elevator.getState() == EState.IDLE && elevator.getEvents().size() == 0)
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
