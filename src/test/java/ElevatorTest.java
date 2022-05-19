import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ElevatorTest
{
    private Elevator elevator;
    private EController eController;

    @BeforeEach
    void setup()
    {
        elevator = new Elevator(8, 0);
        eController = new EController();
        eController.setMaxFloor(8);
        eController.setMinFloor(0);
    }

    @Test
    void test_elevator_increase_capacity_when_elevator_enters_a_src_floor_for_first_time()
    {
        elevator.setCurrentFloor(1);            // Elevator enters floor 1
        elevator.setCurrentCapacity(0);         // Elevator is initially empty

        Event event1 = new Event(1, 1, 2);
        Event event2 = new Event(2, 2, 3);
        Event event3 = new Event(3, 3, 4);

        elevator.getEvents().add(event1);
        elevator.getEvents().add(event2);
        elevator.getEvents().add(event3);

        elevator.updateElevatorCapacity();

        int numPeopleInElevator = elevator.getPeople();
        int numEvents = elevator.getEvents().size();

        assertEquals(1, numPeopleInElevator);       // Check that the num of people in elevator has updated
        assertEquals(3, numEvents);                 // Check that no events have been removed
        assertEquals(true, event1.getSrcReached()); // Check that getSrcReached is trigger to indicate it reached src floor
        assertEquals(false, event2.getSrcReached());// Check that other events have not been altered
        assertEquals(false, event3.getSrcReached());
    }

    @Test
    void test_elevator_decrease_capacity_when_elevator_enters_a_dest_floor_and_src_has_been_reached()
    {
        elevator.setCurrentFloor(5);            // Elevator enters floor 5
        elevator.setCurrentCapacity(2);         // Elevator initially has 2 people inside, it's completing Event 1 job

        Event event1 = new Event(2, 2, 5);
        Event event2 = new Event(6, 6, 8);

        event1.setSrcReached(true);

        elevator.getEvents().add(event1);
        elevator.getEvents().add(event2);

        elevator.updateElevatorCapacity();

        int numPeopleInElevator = elevator.getPeople();
        int numEvents = elevator.getEvents().size();

        assertEquals(0, numPeopleInElevator);       // Check that the num of people in elevator has updated
        assertEquals(1, numEvents);                 // Verify event1 gets removed as it has been completed
        assertEquals(false, event2.getSrcReached());// Check that other events have not been altered
    }

    @Test
    void test_elevator_decrease_and_increase_capacity_when_elevator_enters_a_dest_floor_and_src_floor()
    {
        elevator.setCurrentFloor(5);            // Elevator enters floor 5
        elevator.setCurrentCapacity(2);         // Elevator initially has 2 people inside, it's completing Event 1 job
                                                // Event 2 also needs to take 5 people as it's on floor 5

        Event event1 = new Event(2, 2, 5);
        Event event2 = new Event(6, 5, 8);

        event1.setSrcReached(true);

        elevator.getEvents().add(event1);
        elevator.getEvents().add(event2);

        elevator.updateElevatorCapacity();

        int numPeopleInElevator = elevator.getPeople();
        int numEvents = elevator.getEvents().size();

        assertEquals(6, numPeopleInElevator);       // Check that the num of people in elevator has updated
        assertEquals(1, numEvents);                 // Verify event1 gets removed as it's been completed
        assertEquals(true, event2.getSrcReached());
    }

    @Test
    void test_elevator_capacity_does_not_change_when_it_does_not_enter_a_src_or_destination_floor()
    {
        elevator.setCurrentFloor(1);            // Elevator enters floor 1
        elevator.setCurrentCapacity(0);         // Elevator initially has 0 people inside
        // Event 2 also needs to take 5 people as it's on floor 5

        Event event1 = new Event(2, 2, 5);
        Event event2 = new Event(6, 5, 8);

        elevator.getEvents().add(event1);
        elevator.getEvents().add(event2);

        elevator.updateElevatorCapacity();

        int numPeopleInElevator = elevator.getPeople();
        int numEvents = elevator.getEvents().size();

        assertEquals(0, numPeopleInElevator);       // Check that num of people has NOT changed
        assertEquals(2, numEvents);                 // Verify no events removed
    }

    @Test
    void test_function_reach_all_source_when_all_source_reached_return_true()
    {
        ArrayList<Event> events = new ArrayList<>();

        Event event1 = new Event(2, 2, 5);
        Event event2 = new Event(6, 5, 8);

        events.add(event1);
        events.add(event2);

        event1.setSrcReached(true);
        event2.setSrcReached(true);

        boolean result = elevator.reachedAllSource(events);

        assertEquals(true, result);
    }

    @Test
    void test_function_reach_all_source_when_not_all_source_reached_return_false()
    {
        ArrayList<Event> events = new ArrayList<>();

        Event event1 = new Event(2, 2, 5);
        Event event2 = new Event(6, 5, 8);

        events.add(event1);
        events.add(event2);

        event1.setSrcReached(true);
        event2.setSrcReached(false);

        boolean result = elevator.reachedAllSource(events);

        assertEquals(false, result);
    }

    @Test
    void test_moveFloor_up_from_1_to_2_when_UP_STATE_passed()
    {
        elevator.setCurrentFloor(1);                        // Start at Floor 1

        elevator.moveFloor(EState.UP);                      // Move up to Floor 2

        int currentFloor = elevator.getCurrentFloor();

        assertEquals(2, currentFloor);              // Assert current floor is Floor 2
    }

    @Test
    void test_moveFloor_down_from_2_to_1_when_DOWN_STATE_passed()
    {
        elevator.setCurrentFloor(2);                        // Start at Floor 2

        elevator.moveFloor(EState.DOWN);                    // Move down to Floor 1

        int currentFloor = elevator.getCurrentFloor();

        assertEquals(1, currentFloor);              // Assert current floor is Floor 1
    }

    @Test
    void test_elevator_direction_equals_UP_when_srcFloorIs0_and_destFloorIs10()
    {
        Event event = new Event(10, 0, 10);
        EState direction = elevator.getDirection(event);
        assertEquals(EState.UP, direction);
    }

    @Test
    void test_elevator_direction_equals_DOWN_when_srcFloorIs10_and_destFloorIs0()
    {
        Event event = new Event(10, 10, 0);
        EState direction = elevator.getDirection(event);
        assertEquals(EState.DOWN, direction);
    }

    @Test
    void test_manageMoveState_function_elevator_will_go_down_if_it_reached_max_floor()
    {
        elevator.setMoveState(EState.UP);               // Elevator is currently in a UP state, maxFloor is 8
        elevator.setCurrentFloor(8);

        elevator.manageMoveState();

        EState resultState = elevator.getState();

        assertEquals(EState.DOWN, resultState);
    }

    @Test
    void test_manageMoveState_function_elevator_will_go_idle_if_it_is_at_ground_and_nothing_to_do()
    {
        // Note that the events list in elevator contains nothing inside it by default
        elevator.setMoveState(EState.DOWN);
        elevator.setCurrentFloor(0);

        elevator.manageMoveState();

        EState resultState = elevator.getState();

        assertEquals(EState.IDLE, resultState);
    }

    @Test
    void test_manageMoveState_function_elevator_will_go_up_when_its_idle_and_allocated_a_task()
    {
        elevator.setMoveState(EState.IDLE);
        elevator.setCurrentFloor(0);
        elevator.getEvents().add(new Event(1, 1, 1));  // Add an event

        elevator.manageMoveState();

        EState resultState = elevator.getState();

        assertEquals(EState.UP, resultState);
    }

    @Test
    void test_manageMoveState_function_elevator_will_go_down_when_its_above_min_floor_and_has_nothing_to_do()
    {
        elevator.setMoveState(EState.UP);
        elevator.setCurrentFloor(5);

        elevator.manageMoveState();

        EState resultState = elevator.getState();

        assertEquals(EState.DOWN, resultState);
    }

    @Test
    void test_manageMoveState_function_an_UP_elevator_will_keep_going_up_if_it_hasNot_reached_the_last_destination()
    {
        elevator.setMoveState(EState.UP);
        elevator.setCurrentFloor(5);

        Event event1 = new Event(1, 2, 5);
        Event event2 = new Event(1, 5, 7);
        event1.setSrcReached(true);
        elevator.getEvents().add(event1);
        elevator.getEvents().add(event2);

        elevator.manageMoveState();

        EState resultState = elevator.getState();

        assertEquals(EState.UP, resultState);
    }

    @Test
    void test_manageMoveState_function_a_DOWN_elevator_will_go_down_if_it_has_reached_all_Source_floors()
    {
        elevator.setMoveState(EState.UP);
        elevator.setCurrentFloor(7);

        Event event1 = new Event(1, 7, 2);
        Event event2 = new Event(1, 5, 7); // UP EVENT
        event1.setSrcReached(true);
        event2.setSrcReached(true);

        elevator.getEvents().add(event1);
        elevator.getEvents().add(event2);

        elevator.manageMoveState();

        EState resultState = elevator.getState();

        assertEquals(EState.DOWN, resultState);
    }

    @Test
    void test_manageMoveState_function_a_DOWN_elevator_will_go_up_if_it_has_not_reached_all_Source_floors()
    {
        elevator.setMoveState(EState.UP);
        elevator.setCurrentFloor(5);

        Event event1 = new Event(1, 7, 2);
        Event event2 = new Event(1, 5, 7); // UP EVENT
        event1.setSrcReached(false);
        event2.setSrcReached(true);

        elevator.getEvents().add(event1);
        elevator.getEvents().add(event2);

        elevator.manageMoveState();

        EState resultState = elevator.getState();

        assertEquals(EState.UP, resultState);
    }

    @Test
    void test_manageMoveState_function_a_DOWN_elevator_that_completed_its_job_will_go_down_when_it_complete_all_its_jobs_and_gets_a_new_down_job_below()
    {
        elevator.setMoveState(EState.UP);
        elevator.setCurrentFloor(5);

        Event event1 = new Event(1, 4, 3);
        event1.setSrcReached(false);

        elevator.getEvents().add(event1);

        elevator.manageMoveState();

        EState resultState = elevator.getState();

        assertEquals(EState.DOWN, resultState);
    }

    @Test
    void test_frame_view_to_draw_command()
    {
        elevator.getEvents().add(new Event(1, 1, 2));

        String command = elevator.getCommand();

        assertEquals("(1:2:1) ", command);
    }


}
