import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SchedulerTest
{
    private Scheduler scheduler;
    private ArrayList<Elevator> elevators;
    private Elevator elevator1;
    private Elevator elevator2;
    private  Elevator elevator3;
    private Elevator elevator4;
    private String input;

    @BeforeEach
    void setup()
    {
        elevators = new ArrayList<>();
        elevator1 = new Elevator(8, 1);
        elevator2 = new Elevator(8, 1);
        elevator3 = new Elevator(8, 1);
        elevator4 = new Elevator(8, 1);
        //scheduler = new Scheduler(elevators);
    }
/*
    @Test
    void test_elevator_direction_equals_up_when_srcFloorIs0_and_destFloorIs10()
    {
        Event event = new Event(10, 0, 10);
        EState direction = scheduler.getDirection(event);
        assertEquals(EState.UP, direction);
    }

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
