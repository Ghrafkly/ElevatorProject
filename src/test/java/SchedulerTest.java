import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;


@ExtendWith(MockitoExtension.class)
public class SchedulerTest
{
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
    void setup()
    {
        eController = new EController();
        elevators = new ArrayList<>();
        eController.setMaxFloor(8);
        eController.setMinFloor(0);
        maxCapacity = 8;
        elevator0 = new Elevator(maxCapacity, 0);
        elevator1 = new Elevator(maxCapacity, 1);
        elevators.add(elevator0);
        elevators.add(elevator1);
        scheduler = new Scheduler(elevators, mockGenCommand, mockUserInput);
    }

    @Test
    void test_can_receive_input_fromCommandGen()
    {
        when(mockGenCommand.getCommand()).thenReturn("time 1:2:3");

        String result = mockGenCommand.getCommand();

        assertEquals("time 1:2:3", result);
        verify(mockGenCommand, times(1)).getCommand();
    }

    @Test
    void test_can_receive_input_fromUserInput()
    {
        when(mockUserInput.getUserInput()).thenReturn("time 1:2:3");

        String result = mockUserInput.getUserInput();

        assertEquals("time 1:2:3", result);
        verify(mockUserInput, times(1)).getUserInput();
    }

    @Test
    void test_process_user_input_src2_dest3_numPeople1()
    {
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
    void test_create_event_with_src2_floor3_numPeople1()
    {
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
    void test_elevator_direction_equals_UP_when_srcFloorIs0_and_destFloorIs10()
    {
        Event event = new Event(10, 0, 10);
        EState direction = scheduler.getDirection(event);
        assertEquals(EState.UP, direction);
    }

    @Test
    void test_elevator_direction_equals_DOWN_when_srcFloorIs10_and_destFloorIs0()
    {
        Event event = new Event(10, 10, 0);
        EState direction = scheduler.getDirection(event);
        assertEquals(EState.DOWN, direction);
    }

    @Test
    void test_clean_up_events_with_2_events_and_one_of_them_set_to_delete()
    {
        ArrayList<Event> events = new ArrayList<>();
        Event event1 = new Event(1,  1, 2);
        Event event2 = new Event(1,  1, 2);
        event2.setDelete(true);
        events.add(event1);
        events.add(event2);

        scheduler.cleanUpEvents(events);

        int eventsSize = events.size();

        assertEquals(1, eventsSize);
    }

    @Test
    void test_manageEvent_function_will_add_all_people_in_an_event_to_an_elevator_that_has_sufficient_capacity()
    {
        Event event = new Event(8, 0, 1);
        event.setDelete(false);

        elevator0.setCurrentCapacity(0);
        scheduler.manageEvent(8, event, elevator0);

        assertEquals(true, event.getDelete());      // The event will set to be deleted if it manages to fill up the elevator
    }

    @Test
    void test_manageEvent_function_will_add_partial_people_in_an_event_the_elevator_does_not_have_sufficient_capacity()
    {
        Event event = new Event(9, 0, 1);
        event.setDelete(false);

        elevator0.setCurrentCapacity(0);
        scheduler.manageEvent(8, event, elevator0);

        assertEquals(1, event.getNumPeople());      // The elevator will take 8 people leaving only 1 person
    }

    @Test
    void test_manageEventList_if_all_elevators_are_idle_it_will_assign_a_task_to_the_first_elevator()
    {
        elevator0.setPredictedCapacity(0);                    // Elevators to be at GF by default
        elevator1.setPredictedCapacity(0);
        elevator0.setMoveState(EState.IDLE);
        elevator1.setMoveState(EState.IDLE);

        Event event = new Event(8, 2, 3);           // Add an event to scheduler to be managed

        scheduler.getSchedulerEvents().add(event);

        scheduler.manageEventList(elevator0, scheduler.getSchedulerEvents());

        int result = elevator0.getSchedulerEvents().size();         // The event should be allocated to elevator0 since all are idle

        assertEquals(1, result);
    }

    @Test
    void test_manageEventList_if_elevator_is_returning_to_GF_and_gets_a_down_event_on_route()
    {
        elevator0.setPredictedCapacity(0);            // Elevator is at L5 and returning to GROUND with no jobs
        elevator0.setCurrentFloor(5);
        elevator0.setMoveState(EState.DOWN);

        Event event = new Event(8, 4, 3);       // Scheduler receives an event going from L4 to L3

        scheduler.getSchedulerEvents().add(event);

        scheduler.manageEventList(elevator0, scheduler.getSchedulerEvents());

        int result = elevator0.getSchedulerEvents().size();

        assertEquals(1, result);            // Check the event is received by elevator as its along the way down
    }

    @Test
    void test_manageEventList_if_an_UP_elevator_gets_an_up_event_along_the_way()
    {
        elevator0.setPredictedCapacity(0);            // Elevator is at L2 going to L5
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
    void test_manageEventList_if_a_DOWN_elevator_gets_an_up_event_along_the_way()
    {
        elevator0.setPredictedCapacity(0);
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
    void test_manageEventList_if_a_DOWN_elevator_gets_a_down_event_along_the_way()
    {
        elevator0.setPredictedCapacity(0);
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

    @Test
    void test_manageEventList_2_elevators_1st_is_at_capacity_2nd_elevator_is_available_and_will_take_a_new_job()
    {
        elevator0.setPredictedCapacity(8);            // Elevator0 is travelling from L0 to L8 and has reached max capacity
        elevator0.setCurrentFloor(5);
        elevator0.setMoveState(EState.UP);
        elevator1.setPredictedCapacity(0);            // So Elevator1 will be assigned the job as its idle and doing nothing
        elevator1.setCurrentFloor(0);
        elevator1.setMoveState(EState.IDLE);

        Event event0 = new Event(8, 0, 8);       // Initial event of L0 to L8
        Event event1 = new Event(5, 5, 6);       // Scheduler receives an event going from L5 to L6

        elevator0.getEvents().add(event0);

        scheduler.getSchedulerEvents().add(event1);

        scheduler.manageEventList(elevator0, scheduler.getSchedulerEvents());
        scheduler.manageEventList(elevator1, scheduler.getSchedulerEvents());

        int elevator0events = elevator0.getSchedulerEvents().size();
        int elevator1events = elevator1.getSchedulerEvents().size();

        assertEquals(0, elevator0events);            // Check that elevator0 did not get the job
        assertEquals(1, elevator1events);            // Check that elevator1 got the job
    }

    @Test
    void test_manageEventList_2_elevators_1st_and_2nd_elevators_are_unavailable_and_do_not_get_assigned_jobs()
    {
        elevator0.setPredictedCapacity(8);            // Elevator0 is travelling from L0 to L8 and has reached max capacity
        elevator0.setCurrentFloor(1);
        elevator0.setMoveState(EState.UP);
        elevator1.setPredictedCapacity(8);            // Elevator1 is travelling from L0 to L8 and has reached max capacity
        elevator1.setCurrentFloor(1);
        elevator1.setMoveState(EState.UP);

        Event event0 = new Event(8, 0, 8);       // Initial event of L0 to L8
        Event event1 = new Event(5, 5, 6);       // Scheduler receives an event going from L5 to L6

        elevator0.getEvents().add(event0);
        elevator1.getEvents().add(event0);

        scheduler.getSchedulerEvents().add(event1);

        scheduler.manageEventList(elevator0, scheduler.getSchedulerEvents());
        scheduler.manageEventList(elevator1, scheduler.getSchedulerEvents());

        int elevator0events = elevator0.getSchedulerEvents().size();
        int elevator1events = elevator1.getSchedulerEvents().size();

        assertEquals(0, elevator0events);            // Check that elevator0 did not get the job
        assertEquals(0, elevator1events);            // Check that elevator1 did not get the job
    }

    @Test
    void test_manageEventList_2_elevators_1st_is_unavailable_2nd_is_in_use_but_can_pickup_new_job()
    {
        elevator0.setPredictedCapacity(8);            // Elevator0 is travelling from L0 to L8 and has reached max capacity
        elevator0.setCurrentFloor(1);
        elevator0.setMoveState(EState.UP);
        elevator1.setPredictedCapacity(4);            // Elevator1 has an initial job of Floor 2 to 3 carrying 4 people
        elevator1.setCurrentFloor(1);                 // but it can take another job Floor 4 to 6 carrying 4 people
        elevator1.setMoveState(EState.UP);

        Event event0 = new Event(8, 0, 8);       // Initial event of L0 to L8 for Elevator 0
        Event event1 = new Event(4, 2, 3);       // Initial event of L2 to L3 for Elevator 1
        Event event2 = new Event(4, 4, 6);       // New event of L4 to L6 to be assigned

        elevator0.getEvents().add(event0);
        elevator1.getEvents().add(event1);

        scheduler.getSchedulerEvents().add(event2);

        scheduler.manageEventList(elevator0, scheduler.getSchedulerEvents());
        scheduler.manageEventList(elevator1, scheduler.getSchedulerEvents());

        int elevator0events = elevator0.getSchedulerEvents().size();
        int elevator1events = elevator1.getSchedulerEvents().size();

        assertEquals(0, elevator0events);            // Check that elevator0 did not get the job
        assertEquals(1, elevator1events);            // Check that elevator1 got the job
    }

    @Test
    void test_manageEventList_if_an_UP_elevator_gets_an_up_event_along_the_way_it_can_do_a_partial_pickUP()
    {
        elevator0.setPredictedCapacity(2);            // Elevator is at L2 going to L5 at capacity = 2
        elevator0.setCurrentFloor(2);                 // it then receives an event from L4 to L8 that has 10 people
        elevator0.setMoveState(EState.UP);            // a partial pick is done where 6 people get on the elevator

        Event event0 = new Event(2, 3, 5);        // Initial event of L3 to L5
        Event event1 = new Event(10, 4, 8);       // Scheduler receives an event going from L4 to L8

        elevator0.getEvents().add(event0);
        scheduler.getSchedulerEvents().add(event1);

        scheduler.manageEventList(elevator0, scheduler.getSchedulerEvents());

        int numPeopleAssigned = elevator0.getSchedulerEvents().get(0).getNumPeople();
        int unassignedPeople = scheduler.getSchedulerEvents().get(0).getNumPeople();

        assertEquals(6, numPeopleAssigned);            // 6 people gets assigned to the elevator
        assertEquals(4, unassignedPeople);            // Since a partial pickup is done, there are still 4 people
                                                                    // that needs to be picked up from L4 to L8
    }

    @Test
    void test_whole_system_when_user_inputs_a_command()
    {
        // This is a test to check if the entire system works
        // Scheduler will receive a user input and then pass it to an elevator
        // We will test when all elevators are idle, a user input of 0:8:8 is provided and then is sent to the elevator

        elevator0.setPredictedCapacity(0);                      // Elevator initially has no one in it
        elevator0.setCurrentFloor(0);                           // It's idle and has no occupants
        elevator0.setMoveState(EState.IDLE);

        when(mockUserInput.getUserInput()).thenReturn("time 0:8:8");        // We get a command from user 0:8:8
        when(mockGenCommand.getCommand()).thenReturn("");

        scheduler.listen();         // The scheduler listens for the command

        int elevator0numEvents = elevator0.getSchedulerEvents().size();     // We check that elevator 0 gets the command

        assertEquals(1, elevator0numEvents);
    }

    @Test
    void test_whole_system_when_commandGen_sends_a_command()
    {
        // This is a test to check if the entire system works
        // Scheduler will receive a command gen input and then pass it to an elevator
        // We will test when all elevators are idle, a user input of 0:8:8 is provided and then is sent to the elevator

        elevator0.setPredictedCapacity(0);                      // Elevator initially has no one in it
        elevator0.setCurrentFloor(0);                           // It's idle and has no occupants
        elevator0.setMoveState(EState.IDLE);

        when(mockGenCommand.getCommand()).thenReturn("time 0:8:8");        // We get a command from user 0:8:8
        when(mockUserInput.getUserInput()).thenReturn("");

        scheduler.listen();         // The scheduler listens for the command

        int elevator0numEvents = elevator0.getSchedulerEvents().size();     // We check that elevator 0 gets the command

        assertEquals(1, elevator0numEvents);
    }

    @Test
    void test_whole_system_when_no_input_is_given_but_there_are_stacked_up_events_that_are_not_assigned_yet()
    {
        elevator0.setPredictedCapacity(0);                      // Elevator initially has no one in it
        elevator0.setCurrentFloor(0);                           // It's idle and has no occupants
        elevator0.setMoveState(EState.IDLE);

        scheduler.getSchedulerEvents().add(new Event(8, 0, 8));   // There's an event not assigned yet

        when(mockGenCommand.getCommand()).thenReturn("");       // No input
        when(mockUserInput.getUserInput()).thenReturn("");      // No input

        scheduler.listen();         // The scheduler listens for the command

        int elevator0numEvents = elevator0.getSchedulerEvents().size();     // We check that elevator 0 gets the command

        assertEquals(1, elevator0numEvents);
    }
}
