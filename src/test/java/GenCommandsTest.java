import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GenCommandsTest {

    private GenCommands genCommands;

    @BeforeEach
    void setUp() {
        EController.maxFloor = 3;
        EController.minFloor = 0;
        genCommands = new GenCommands();
    }

    @Test
    void test_getCommand() {
        genCommands.setCommand("1:2:3");
        assertEquals("null 1:2:3", genCommands.getCommand());
    }

    @Test
    void test_morning_sim() {
        for (int i = 0; i < 1000; i++) {
            String temp = genCommands.morningSim(2,0);
            String[] test = temp.split(":");
            assertEquals("0", test[0]);
        }
    }

    @Test
    void test_afternoon_sim() {
        for (int i = 0; i < 1000; i++) {
            String temp = genCommands.afternoonSim(2,0);
            String[] test = temp.split(":");
            assertEquals("0", test[1]);
        }
    }

    @Test
    void test_normal_sim() {
        for (int i = 0; i < 1000; i++) {
            String temp = genCommands.normalSim(2);
            String[] test = temp.split(":");
            assertNotEquals(test[0], test[1]);
        }
    }
}
