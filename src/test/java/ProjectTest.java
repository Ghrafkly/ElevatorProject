import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectTest {

    Validator validate;


    @BeforeEach
    void setUp(){
        validate = new Validator();
    }

    @Test
    void test_validate_returns_false_when_empty() throws InterruptedException {

        assertFalse(validate.valCommand(" "));

    }

    @Test
    void test_validate_returns_false_with_alphabetic_input() throws InterruptedException {
        assertFalse(validate.valCommand("a:b:c"));
    }

    @Test
    void test_validate_returns_false_with_mixed_input() throws InterruptedException {
        assertFalse(validate.valCommand("10:b:c"));
    }

    @Test
    void test_validate_returns_false_with_mixed_input_per_branch() throws InterruptedException {
        assertFalse(validate.valCommand("10a:b:c"));
    }

    @Test
    void test_validate_returns_true_with_4_2_4() throws InterruptedException {
        EController.maxFloor = 10;
        EController.minFloor = 0;

        assertTrue(validate.valCommand("4:2:4"));
    }

    @Test
    void test_validate_returns_true_with_1_2_4() throws InterruptedException {
        EController.maxFloor = 10;
        EController.minFloor = 0;

        assertTrue(validate.valCommand("1:2:4"));
    }

    @Test
    void test_validate_returns_false_with_1_1_4() throws InterruptedException {
        EController.maxFloor = 10;
        EController.minFloor = 0;

        assertFalse(validate.valCommand("1:1:4"));
    }

    @Test
    void test_validate_returns_false_with_1_2_8_9() throws InterruptedException {
        assertFalse(validate.valCommand("1:2:8:9"));
    }

    @Test
    void test_validate_returns_false_with_1_2_8_colon() throws InterruptedException {
        assertFalse(validate.valCommand("1:2:8:"));
    }

    @Test
    void test_validate_returns_false_with_1_point_5_2_8() throws InterruptedException {
        assertFalse(validate.valCommand("1.5:2:8"));
    }

    @Test
    void test_validate_returns_false_with_2_10_point_5_8() throws InterruptedException {
        assertFalse(validate.valCommand("2:10.5:8"));
    }

    @Test
    void test_validate_returns_false_with_2_8_10_point_5() throws InterruptedException {
        assertFalse(validate.valCommand("2:8:10.5"));
    }

    @Test
    void test_validate_returns_false_with_2_8() throws InterruptedException {
        assertFalse(validate.valCommand("2:8"));
    }

    @Test
    void test_user_validate_returns_false_when_empty() throws InterruptedException {

        assertFalse(validate.valCommand(" "));

    }

    @Test
    void test_user_validate_returns_false_when_wrong_format() throws InterruptedException {

        assertFalse(validate.valCommand("2:2:3")); //Checks final else condition

    }

    @Test
    void test_user_validate_returns_false_less_min_value_wrong_format() throws InterruptedException {
        EController.minFloor = 5;

        assertFalse(validate.valCommand("2:3:3")); //Checks final else condition

    }
    @Test
    void test_user_validate_returns_false_greater_max_value_wrong_format() throws InterruptedException {
        EController.maxFloor = 1;
        EController.minFloor = 0;
        assertFalse(validate.valCommand("2:3:3")); //Checks final else condition

    }

    @Test
    void test_input_containing_colon_returns_command() throws InterruptedException {
        EController.maxFloor = 10;
        EController.minFloor = 0;

        assertEquals("command", validate.valInput("3:2:4"));
    }

    @Test
    void test_containing_numeric_returns_interval() throws InterruptedException {
        assertEquals("interval", validate.valInput(String.valueOf(204)));
    }

    @Test
    void test_containing_numeric_returns_empty_string() throws InterruptedException {
        assertEquals("", validate.valInput(String.valueOf(24)));
    }

    @Test
    void test_containing_morning_returns_simulation() throws InterruptedException {
        assertEquals("simulation", validate.valInput("morning"));
    }



    @Test
    void test_containing_morning_2_returns_simulation() throws InterruptedException {
        assertEquals("simulation", validate.valInput("morning 10"));
    }

    @Test
    void test_containing_afternoon_returns_simulation() throws InterruptedException {
        assertEquals("simulation", validate.valInput("afternoon"));
    }

    @Test
    void test_containing_normal_returns_simulation() throws InterruptedException {
        assertEquals("simulation", validate.valInput("normal"));
    }

    @Test
    void test_containing_8_returns_false() throws InterruptedException {
        assertFalse(validate.valSimulation("8"));
    }

    @Test
    void test_containing_8_normal_returns_false() throws InterruptedException {
        assertFalse(validate.valSimulation("8 normal"));
    }

    @Test
    void test_containing_8normal_returns_false() throws InterruptedException {
        assertFalse(validate.valSimulation("8normal"));
    }

    @Test
    void test_containing_normal_8_returns_true() throws InterruptedException {
        assertTrue(validate.valSimulation("normal 8"));
    }

    @Test
    void test_containing_morning_8_returns_true() throws InterruptedException {
        assertTrue(validate.valSimulation("morning 8"));
    }

    @Test
    void test_containing_normal_comma_8_returns_false() throws InterruptedException {
        assertFalse(validate.valSimulation("normal,8"));
    }

    @Test
    void test_getCommands_returns_list_of_commands() throws InterruptedException {
        ArrayList<String> AnsCommand = new ArrayList<>();
        AnsCommand.add("2:3:4");
        AnsCommand.add("9:3:2");

        ArrayList<String> command = new ArrayList<>();
        command.add("2:3:4");
        command.add("9:3:2");

        validate.setCommands(command);

        assertEquals(AnsCommand, validate.getCommands());
    }

    @Test
    void test_minFloor_less_maxFloor_returns_true(){
        EController.maxFloor = 10;
        EController.minFloor = 0;
        assertTrue(validate.valConfig());
    }

    @Test
    void test_minFloor_equals_maxFloor_returns_false(){
        EController.maxFloor = 10;
        EController.minFloor = 10;
        assertFalse(validate.valConfig());
    }

    @Test
    void test_minFloor_greater_maxFloor_returns_false(){
        EController.maxFloor = 10;
        EController.minFloor = 11;
        assertFalse(validate.valConfig());
    }

}
