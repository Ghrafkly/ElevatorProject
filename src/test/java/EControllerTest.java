import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EControllerTest {

    EController eController;

    @BeforeEach
    void setUp() {
        eController = new EController();
        eController.setMaxFloor(10);
        eController.setMinFloor(0);
        eController.setCapacity(8);
        eController.setNumberOfElevators(5);
    }

    @Test
    void test_getMaxFloor() {
        assertEquals(10, eController.getMaxFloor());
    }

    @Test
    void test_getMinFloor() {
        assertEquals(0, eController.getMinFloor());
    }

    @Test
    void test_getNumberOfElevators() {
        assertEquals(5, eController.getNumberOfElevators());
    }

    @Test
    void getElevators() {
        eController.setElevatorThreads();
        assertEquals(5, eController.getElevators().size());
    }

    @Test
    void test_capacity() {
        assertEquals(8, EController.capacity);
    }

    @Test
    void test_runElevators() {
        eController.setElevatorThreads();
        eController.runElevators();

        eController.getThreads()
                .stream()
                .map(Thread::isAlive)
                .forEach(Assertions::assertTrue);

        eController.getThreads().forEach(Thread::interrupt);
    }
}