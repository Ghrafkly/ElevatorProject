import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class SchedulerTest {
    private Scheduler scheduler;
    private ArrayList<Elevator> elevators;
    private Elevator elevator0;
    private Elevator elevator1;
    private EController eController;
    private int maxCapacity;

    @Mock
    GenCommands mockGenCommand;

    @Mock
    UserInput mockUserInput;

    @BeforeEach
    void setup() {
        eController = new EController();
        elevators = new ArrayList<>();
        eController.setMaxFloor(8);
        eController.setMinFloor(0);
        maxCapacity = 8;
        elevator0 = new Elevator(maxCapacity, 0);
        elevator1 = new Elevator(maxCapacity, 1);
        scheduler = new Scheduler(elevators, mockGenCommand, mockUserInput);
    }

    @Test
    void test_process_user_input_src2_dest3_numPeople1() {
        String processedInput = scheduler.processUserInput("time 1:2:3");
        Event event = scheduler.getSchedulerEvents().get(0);
        int eventSrc = event.getSrc();
        int eventDest = event.getDest();
        int numPeople = event.getNumPeople();

        // Check that the string was processed correctly
        assertEquals("1:2:3", processedInput);
        // Check that the event was added correctly
        assertEquals(3, numPeople);
        assertEquals(1, eventSrc);
        assertEquals(2, eventDest);
    }

    @Test
    void test_create_event_with_src2_floor3_numPeople1() {
        String input = "1:2:3";
        Event event = scheduler.createEvent(input);
        int eventSrc = event.getSrc();
        int eventDest = event.getDest();
        int numPeople = event.getNumPeople();

        assertEquals(3, numPeople);
        assertEquals(1, eventSrc);
        assertEquals(2, eventDest);
    }

    @Test
    void test_elevator_direction_equals_UP_when_srcFloorIs0_and_destFloorIs10() {
        Event event = new Event(10, 0, 10);
        EState direction = scheduler.getDirection(event);
        assertEquals(EState.UP, direction);
    }

    @Test
    void test_elevator_direction_equals_DOWN_when_srcFloorIs10_and_destFloorIs0() {
        Event event = new Event(10, 10, 0);
        EState direction = scheduler.getDirection(event);
        assertEquals(EState.DOWN, direction);
    }

    @Test
    void test_clean_up_events_with_2_events_and_one_of_them_set_to_delete() {
        ArrayList<Event> events = new ArrayList<>();
        Event event1 = new Event(1, 1, 2);
        Event event2 = new Event(1, 1, 2);
        event2.setDelete(true);
        events.add(event1);
        events.add(event2);

        scheduler.cleanUpEvents(events);

        int eventsSize = events.size();

        assertEquals(1, eventsSize);
    }

    @Test
    void test_manageEvent_function_will_add_all_people_in_an_event_to_an_elevator_that_has_sufficient_capacity() {
        Event event = new Event(8, 0, 1);
        event.setDelete(false);

        elevator0.setCurrentCapacity(0);
        scheduler.manageEvent(8, event, elevator0);

        assertTrue(event.getDelete());      // The event will set to be deleted if it manages to fill up the elevator
    }

    @Test
    void test_manageEvent_function_will_add_partial_people_in_an_event_the_elevator_does_not_have_sufficient_capacity() {
        Event event = new Event(9, 0, 1);
        event.setDelete(false);

        elevator0.setCurrentCapacity(0);
        scheduler.manageEvent(8, event, elevator0);

        assertEquals(1, event.getNumPeople());      // The elevator will take 8 people leaving only 1 person
    }

    @Test
    void test_manageEventList_if_all_elevators_are_idle_it_will_assign_a_task_to_the_first_elevator() {
        elevator0.setCurrentCapacity(0);                    // Elevators to be at GF by default
        elevator1.setCurrentCapacity(0);
        elevator0.setMoveState(EState.IDLE);
        elevator1.setMoveState(EState.IDLE);

        Event event = new Event(8, 2, 3);           // Add an event to scheduler to be managed

        scheduler.getSchedulerEvents().add(event);

        scheduler.manageEventList(elevator0, scheduler.getSchedulerEvents());

        int result = elevator0.getSchedulerEvents().size();         // The event should be allocated to elevator0 since all are idle

        assertEquals(1, result);
    }

    @Test
    void test_manageEventList_if_elevator_is_returning_to_GF_and_gets_a_down_event_on_route() {
        elevator0.setCurrentCapacity(0);            // Elevator is at L5 and returning to GROUND with no jobs
        elevator0.setCurrentFloor(5);
        elevator0.setMoveState(EState.DOWN);

        Event event = new Event(8, 4, 3);       // Scheduler receives an event going from L4 to L3

        scheduler.getSchedulerEvents().add(event);

        scheduler.manageEventList(elevator0, scheduler.getSchedulerEvents());

        int result = elevator0.getSchedulerEvents().size();

        assertEquals(1, result);            // Check the event is received by elevator as its along the way down
    }

    @Test
    void test_manageEventList_if_an_UP_elevator_gets_an_up_event_along_the_way() {
        elevator0.setCurrentCapacity(0);            // Elevator is at L2 going to L5
        elevator0.setCurrentFloor(2);
        elevator0.setMoveState(EState.UP);

        Event event0 = new Event(2, 3, 5);       // Initial event of L3 to L5
        Event event1 = new Event(2, 4, 8);       // Scheduler receives an event going from L4 to L8

        elevator0.getEvents().add(event0);
        scheduler.getSchedulerEvents().add(event1);

        scheduler.manageEventList(elevator0, scheduler.getSchedulerEvents());

        int result = elevator0.getSchedulerEvents().size();

        assertEquals(1, result);            // Check the event is received by elevator as its along the way up
    }

    @Test
    void test_manageEventList_if_a_DOWN_elevator_gets_an_up_event_along_the_way() {
        elevator0.setCurrentCapacity(0);
        elevator0.setCurrentFloor(1);                              // Current at L1
        elevator0.setMoveState(EState.UP);

        Event event0 = new Event(2, 8, 0);       // Initial event of L8 to L0
        Event event1 = new Event(2, 4, 8);       // Scheduler receives an event going from L4 to L8

        elevator0.getEvents().add(event0);
        scheduler.getSchedulerEvents().add(event1);

        scheduler.manageEventList(elevator0, scheduler.getSchedulerEvents());

        int result = elevator0.getSchedulerEvents().size();

        assertEquals(1, result);            // Check the event is received by elevator as its along the way up
    }

    @Test
    void test_manageEventList_if_a_DOWN_elevator_gets_a_down_event_along_the_way() {
        elevator0.setCurrentCapacity(0);
        elevator0.setCurrentFloor(8);                              // Current at L8
        elevator0.setMoveState(EState.DOWN);

        Event event0 = new Event(2, 4, 3);       // Initial event of L6 to L4
        Event event1 = new Event(1, 3, 2);       // Scheduler receives an event going from L2 to L1

        elevator0.getEvents().add(event0);
        scheduler.getSchedulerEvents().add(event1);

        scheduler.manageEventList(elevator0, scheduler.getSchedulerEvents());

        int result = elevator0.getSchedulerEvents().size();

        assertEquals(1, result);            // Check the event is received by elevator as its along the way down
    }
}
