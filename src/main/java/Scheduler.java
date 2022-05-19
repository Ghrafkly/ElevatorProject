import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class Scheduler implements Runnable
{
    private static final Logger LOGGER = LogManager.getLogger(Scheduler.class);
    private final ArrayList<Elevator> elevators;        // ArrayList of elevator objects being managed
    private String genCommandInput;                     // The input received from GenCommand (random)
    private String userInput;                           // The input received from UserInput (user)
    private String tempGenCommandInput;                 // A temporary string to track previous GenCommand input
    private String tempUserInput;                       // A temporary string to track previous UserInput input
    private ArrayList<Event> schedulerEvents;           // An ArrayList of events received from inputs
    private GenCommands genCommands;                    // An instance of the GenCommand object
    private UserInput userInputObj;                     // An instance of the UserInput object

    /**
     * Default constructor for Scheduler Class
     * @param elevators the arrayList of elevator objects being managed
     * @param genCommands instance of GenCommands used to generate random inputs
     * @param userInputObj instance of UserInput used to retrieve user inputs
     */
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

    /**
     * Implementation of Runnable
     */
    @Override
    public void run()
    {
        // Main loop
        while(true)
        {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tempGenCommandInput = genCommands.getCommand();
            tempUserInput = userInputObj.getUserInput();

            // Check to see if the GenCommand input is new
            if(!tempGenCommandInput.equals(genCommandInput) && !tempGenCommandInput.isBlank())
            {
                genCommandInput = tempGenCommandInput;
                processUserInput(genCommandInput);

                // Loop through all the elevators and assign Events as required
                for (Elevator elevator : elevators)
                {
                    manageEventList(elevator, schedulerEvents);
                }
            }

            // Check to see if the UserInput input is new
            if(!tempUserInput.equals(userInput) && !tempUserInput.isBlank())
            {
                userInput = tempUserInput;
                processUserInput(userInput);

                // Loop through all the elevators and assign Events as required
                for (Elevator elevator : elevators) {
                    // Assign events to the appropriate elevator
                    manageEventList(elevator, schedulerEvents);
                }
            }

            // Check to see if there are any leftover events in schedulerEvents that need to be processed
            if(schedulerEvents.size() > 0)
            {
                // Loop through all the elevators and assign Events as required
                for (Elevator elevator : elevators)
                {
                    manageEventList(elevator, schedulerEvents);
                }
            }
        }
    }

    /**
     * Getter for scheduler events, this will be used mostly for testing purposes.
     * @return list of scheduler events
     */
    public ArrayList<Event> getSchedulerEvents()
    {
        return schedulerEvents;
    }

    /**
     * This function will convert userInput into Event objects and append to scheduler's events to be allocated to each Elevator
     * @param input in the form of "time src:dest:numPeople"
     */
    public String processUserInput(String input)
    {
        String[] userInput = input.split(" ");
        String command = userInput[1];
        Event event = createEvent(command);
        schedulerEvents.add(event);

        return command;
    }

    /**
     * This function takes a single String input in the form src:dest:numPeople and returns an Event object
     * @param input string in the form src:destination:numPeople
     * @return event object
     */
    public Event createEvent(String input)
    {
        String[] inputs = input.split(":");

        int src = Integer.parseInt(inputs[0]);
        int dest = Integer.parseInt(inputs[1]);
        int numPeople = Integer.parseInt(inputs[2]);

        return new Event(numPeople, src, dest); // -1 elevatorID means it's not assigned yet
    }

    /**
     * This function will get the direction the elevator needs to travel in to complete an Event journey
     * @return either UP or DOWN
     */
    public EState getDirection(Event event)
    {
        return event.getDest() > event.getSrc()
                ? EState.UP
                : EState.DOWN;
    }

    /**
     * This function is used to allocate a list of events to an Elevator. The logic we have adopted is as follows:
     * A elevator will either be a UP or DOWN elevator.
     * UP elevator means its responsible for getting events that go UP.
     * DOWN elevator means its responsible for getting events that go DOWN.
     * A DOWN elevator will try to grab UP events only if it's along the way.
     * @param elevator is the elevator being queried
     * @param schedulerEvents are all the events that needs to be processed by the scheduler
     */
    public void manageEventList(Elevator elevator, ArrayList<Event> schedulerEvents)
    {
        int maxCapacity = elevator.MAX_CAPACITY;

        for (Event event : schedulerEvents)
        {
            int currFloor = elevator.getCurrentFloor();             // The current floor of the elevator
            int eventSrc = event.getSrc();                          // The source floor of event
            int eventDest = event.getDest();                        // The destination floor of event
            EState eventDir = getDirection(event);                  // The direction from SRC to DEST

            /*
             * Here we check if the elevator has completed all it's jobs and is returning to the ground floor.
             * If so it will grab events that are along the way for it on the way down to the ground floor.
             */
            if(elevator.getEvents().size() == 0 &&
                    elevator.getState() == EState.DOWN &&
                    eventDir == EState.DOWN &&
                    currFloor > event.getSrc())
            {
                manageEvent(maxCapacity, event, elevator);
            }

            // If the elevator has already been allocated a job we do the following:
            else if(elevator.getEvents().size() > 0)
            {
                // As explained before, Elevator is considered a UP or DOWN elevator. We determine this by looking at the first event its given
                EState direction = getDirection(elevator.getEvents().get(0));
                int maxDest = elevator.getEvents().get(0).getDest();

                // If it's a UP elevator then grab jobs along the way that go UP also
                if (direction == EState.UP &&
                        getDirection(event) == EState.UP &&
                        currFloor < eventSrc)
                {
                    manageEvent(maxCapacity, event, elevator);
                }

                // If it's a DOWN elevator then grab UP jobs that are along the way
                else if (direction == EState.DOWN &&
                        eventDir == EState.UP &&
                        elevator.getState() == EState.UP &&
                        currFloor < eventSrc &&
                        maxDest < eventSrc)
                {
                    manageEvent(maxCapacity, event, elevator);
                }

                // If it's a DOWN elevator that's going down then pick up DOWN jobs along the way
                else if(direction == EState.DOWN &&
                        eventDir == EState.DOWN &&
                        elevator.getState() == EState.DOWN &&
                        currFloor > eventSrc)
                {
                    manageEvent(maxCapacity, event, elevator);
                }

            }

            // Else if there are no other elevators available and the current Elevator is IDLE and has no jobs, use it
            else if (elevator.getState() == EState.IDLE && (elevator.getSchedulerEvents().size() + elevator.getEvents().size() == 0))
            {
                manageEvent(maxCapacity, event, elevator);
            }
        }

        // Remove and schedulerEvents that have been fully processed
        cleanUpEvents(schedulerEvents);
    }

    /**
     * This function will assign a group of people into an elevator
     * @param maxCapacity the maximum capacity of the elevator
     * @param event the event being processed
     * @param elevator the elevator being considered for assignment
     */
    public void manageEvent(int maxCapacity, Event event, Elevator elevator)
    {
        // The elevator contains an ArrayList of events called schedulerEvents that contain events
        // to be processed by the elevator. These may not be processed yet.
        int unprocessedPeople = elevator.getSchedulerEvents().stream().mapToInt(x -> x.getNumPeople()).sum();
        int numUnprocessedEvents = elevator.getSchedulerEvents().size();

        if(numUnprocessedEvents > 0)
        {
            // Elevator to take as many as it can before exceeding the max capacity
            if(elevator.getPredictedCapacity() + unprocessedPeople + event.getNumPeople() > maxCapacity)
            {
                int numPeopleCanAdd = maxCapacity - elevator.getPredictedCapacity() - unprocessedPeople;
                if(numPeopleCanAdd != 0)
                {
                    Event elevatorEvent = new Event(numPeopleCanAdd, event.getSrc(), event.getDest());
                    elevator.addEvent(elevatorEvent);
                    event.setNumPeople(event.getNumPeople() - numPeopleCanAdd);
                }
            }

            // Elevator will add all the people
            else if(elevator.getPredictedCapacity() + unprocessedPeople + event.getNumPeople() <= maxCapacity)
            {
                elevator.addEvent(new Event(event.getNumPeople(), event.getSrc(), event.getDest()));
                event.setDelete(true);
            }
        }

        else if(numUnprocessedEvents == 0)
        {
            if(elevator.getPredictedCapacity() + event.getNumPeople() > maxCapacity)
            {
                int numPeopleCanAdd = maxCapacity - elevator.getPredictedCapacity();
                if(numPeopleCanAdd != 0)
                {
                    Event elevatorEvent = new Event(numPeopleCanAdd, event.getSrc(), event.getDest());
                    elevator.addEvent(elevatorEvent);
                    event.setNumPeople(event.getNumPeople() - numPeopleCanAdd);
                }

            }
            else if(elevator.getPredictedCapacity() + event.getNumPeople() <= maxCapacity)
            {
                elevator.addEvent(new Event(event.getNumPeople(), event.getSrc(), event.getDest()));
                event.setDelete(true);
            }
        }

    }

    /**
     * This function will remove any events that have the boolean 'remove' turned on
     * @param events are an ArrayList of events needing to be cleaned
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
