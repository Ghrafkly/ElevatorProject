import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SchedulerTest
{
    Scheduler scheduler;
    ArrayList<Elevator> elevators;

    @BeforeEach
    void setup()
    {
        scheduler = new Scheduler();
        elevators = new ArrayList<>();
    }

    @Test
    void test_elevator_direction_equals_up_when_srcFloorIs0_and_destFloorIs10()
    {
        Event event = new Event(10, 0, 10);
        EState direction = scheduler.getDirection(event);
        assertEquals(EState.UP, direction);
    }

    @Test
    void test()
    {

    }
}
