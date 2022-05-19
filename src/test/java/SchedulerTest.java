import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SchedulerTest
{
    private Scheduler scheduler;
    private ArrayList<Elevator> elevators;
    private Elevator elevator1;
    private Elevator elevator2;
    private  Elevator elevator3;
    private Elevator elevator4;

    @Mock
    GenCommands mockGenCommand;

    @Mock
    UserInput mockUserInput;

    @BeforeEach
    void setup()
    {
        elevators = new ArrayList<>();
        scheduler = new Scheduler(elevators, mockGenCommand, mockUserInput);
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
        assertEquals(1, numPeople);
        assertEquals(2, eventSrc);
        assertEquals(3, eventDest);
    }

    @Test
    void test_create_event_with_src2_floor3_numPeople1()
    {
        String input = "1:2:3";
        Event event = scheduler.createEvent(input);
        int eventSrc = event.getSrc();
        int eventDest = event.getDest();
        int numPeople = event.getNumPeople();

        assertEquals(1, numPeople);
        assertEquals(2, eventSrc);
        assertEquals(3, eventDest);
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
    void test_clean_up_events()
    {
        Event event1 = new Event(1,  1, 2);
        Event event2 = new Event(1,  1, 2);
    }

    /*
    @Test
    void test_manage_move_state_when_elevator_idle_and_needs_to_complete_a_go_down_event()
    {
        elevator1.addEvent(new Event(0, 0, 2));
        elevator1.addEvent(new Event(2, 2, 1));
        elevators.add(elevator1);
       // scheduler.manageMoveState(elevators);
        assertEquals(EState.UP, elevator1.getMoveState());
    }

    @Test
    void test_manage_move_state_when_elevator_is_at_min_floor_and_has_no_events()
    {
        elevators.add(elevator1);
        //scheduler.manageMoveState(elevators);
        assertEquals(EState.IDLE, elevator1.getMoveState());
    }

 */
}
