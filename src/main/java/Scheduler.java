import java.util.ArrayList;
import java.util.Iterator;

public class Scheduler implements Runnable {
    ArrayList<Event> events;
    ArrayList<Elevator> elevators;
    String inputs;

    public Scheduler(ArrayList<Elevator> elevators) {
        events = new ArrayList<>();
        this.elevators = elevators;
    }

    @Override
    public void run() {
        int numElevators = elevators.size();

        inputs = "1:2:3";

        while (true) {
            processInput(inputs, elevators);

            int numIdleElevators = getNumElevatorUsingState(EState.IDLE, elevators);

            for (Elevator elevator : elevators) {
                switch (elevator.getMoveState()) {
                    case DOWN:
                        moveDownAFloor(elevator);
                        break;
                    case UP:
                        moveUpAFloor(elevator);
                        break;
                    case IDLE:
                        break;
                }

                //
                if (elevator.getEvents().size() == 0) {

                }
            }
        }
    }

    public int getNumElevatorUsingState(EState eState, ArrayList<Elevator> elevators) {
        int sum = 0;
        for (Elevator elevator : elevators) {
            if (elevator.getMoveState() == eState) {
                sum += 1;
            }
        }
        return sum;
    }

    /**
     * This function will convert inputs into Event objects and appended to Elevator's jobs
     *
     * @param inputs    in the form of src:dest:numPeople comma-separated
     * @param elevators contains array of the elevators
     */
    public void processInput(String inputs, ArrayList<Elevator> elevators) {
        String[] inputsArr = inputs.split(",");
        for (String input : inputsArr) {
            String inputStr = input.split(":")[0];
            Event event = createEvent(input);
            events.add(event);
        }
    }

    /**
     * This function takes a single String input in the form src:dest:numPeople and returns an Event object
     *
     * @param input string in the form src:destination:numPeople
     * @return event object
     */
    public Event createEvent(String input) {
        int src = Integer.parseInt(input.split(":")[0]);
        int dest = Integer.parseInt(input.split(":")[1]);
        int numPeople = Integer.parseInt(input.split(":")[2]);

        return new Event(numPeople, src, dest);
    }

    /**
     * @return the direction the elevator needs to travel in
     */
    public EState getDirection(Event event) {
        return event.getDest() > event.getSrc()
                ? EState.UP
                : EState.DOWN;
    }

    public void moveUpAFloor(Elevator elevator) {
        elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);
    }

    public void moveDownAFloor(Elevator elevator) {
        elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);
    }

    /**
     * This function will get all inputs that are travelling in the same direction as the elevator and can be reached
     *
     * @param elevator is the object being queried
     * @param events   are all the events that needs to be processed
     * @return
     */
    public ArrayList<Event> getAvailableOnRouteElevators(Elevator elevator, ArrayList<Event> events) {
        EState currState = elevator.getMoveState();
        int currFloor = elevator.getCurrentFloor();
        ArrayList<Integer> destFloors = new ArrayList<>();
        int currCapacity = elevator.getCurrentCapacity();
        int maxCapacity = elevator.MAX_CAPACITY;

        for (Event event : events) {
            /*
             * Here we check if :
             * 1. the input is on route for the elevator
             * 2. the elevator has sufficient capacity
             */
            if (currState == EState.UP
                    && currFloor < event.getSrc()
                    && (currCapacity + event.getNumPeople() <= maxCapacity)) {
                elevator.addEvent(event);
                event.setDelete(true);
            } else if (currState == EState.DOWN
                    && currFloor > event.getSrc()
                    && (currCapacity + event.getNumPeople() <= maxCapacity)) {
                elevator.addEvent(event);
                event.setDelete(true);
            }
        }

        return events;
    }

    public void removeToDeleteEvents(ArrayList<Event> events) {
        for (Iterator<Event> it = events.iterator(); it.hasNext(); )
            if (it.next().getDelete())
                it.remove();

        //        events.removeIf(Event::getDelete);
    }
}
