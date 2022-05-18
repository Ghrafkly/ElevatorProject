import java.util.ArrayList;

/*
    Rip out MAX_CAPACITY from creating the elevator.
    Put it into another class, such as scheduler.
 */

public class EController implements Runnable {
    public static int maxFloor;
    public static int minFloor;
    public static int capacity;
    private int numberOfElevators;

    private FrameView fm;
    private final ArrayList<Elevator> elevators = new ArrayList<>();
    private final ArrayList<Thread> threads = new ArrayList<>();
    private ArrayList<Event> eControllerEvents = new ArrayList<>();

    public String messageSend;

    public EController() {
    }

    public void setMaxFloor(int maxFloor) {
        EController.maxFloor = maxFloor;
    }

    public void setMinFloor(int minFloor) {
        EController.minFloor = minFloor;
    }

    public int getMaxFloor() {
        return EController.maxFloor;
    }

    public int getMinFloor() {
        return EController.minFloor;
    }

    public void setCapacity(int capacity) {
        EController.capacity = capacity;
    }

    public void setNumberOfElevators(int numberOfElevators) {
        this.numberOfElevators = numberOfElevators;
    }

    public int getNumberOfElevators(){
        return numberOfElevators;
    }

    // Need to link the Elevator object to a thread somehow
    public void setElevatorThreads(FrameView fm) {
        for (int i = 0; i < numberOfElevators; i++) {
            elevators.add(new Elevator(capacity, i, fm));
        }
    }

    public void runElevators() {
        for (int i = 0; i < elevators.size(); i++) {
            threads.add(new Thread(elevators.get(i), String.valueOf(i)));
        }
        threads.forEach(Thread::start);
    }

    public void input() {

    }

    public ArrayList<Elevator> getElevators() {
        return elevators;
    }

    public ArrayList<Event> getEcontrollerEvents()
    {
        return eControllerEvents;
    }

    public void assignEvents()
    {
        for(Elevator elevator : elevators)
        {
            for(Event event : eControllerEvents)
            {
                if(elevator.getELEVATOR_ID() == event.getElevatorId())
                {
                    elevator.addEvent(event);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ElevatorController{" +
                "maxFloor=" + maxFloor +
                ", minFloor=" + minFloor +
                ", numberOfElevators=" + numberOfElevators +
                ", capacity=" + capacity +
                ", elevators=" + elevators +
                '}';
    }

    @Override
    public void run() {
        while(true)
        {
            if(eControllerEvents.size() > 0)
            {
                assignEvents();
            }
        }
    }
}
