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

@ExtendWith(MockitoExtension.class)
public class ProjectTest {

    Validator validate;
    UserInput user;

    @Mock
    GenCommands gc = mock(GenCommands.class);;

    @BeforeEach
    void setUp(){

        validate = new Validator();
        user = new UserInput();

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
        assertTrue(validate.valCommand("4:2:4"));
    }

    @Test
    void test_validate_returns_true_with_1_2_4() throws InterruptedException {
        assertTrue(validate.valCommand("1:2:4"));
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
    void test_input_containing_colon_returns_command() throws InterruptedException {
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
    void test_containing_24_returns_false() throws InterruptedException {
        assertFalse(validate.valSimulation("24"));
    }

    @Test
    void test_containing_24_normal_returns_false() throws InterruptedException {
        assertFalse(validate.valSimulation("24 normal"));
    }

    @Test
    void test_containing_24normal_returns_false() throws InterruptedException {
        assertFalse(validate.valSimulation("24normal"));
    }

    @Test
    void test_containing_normal_24_returns_true() throws InterruptedException {
        assertFalse(validate.valSimulation("normal 24"));
    }

    @Test
    void test_containing_morning_24_returns_true() throws InterruptedException {
        assertTrue(validate.valSimulation("morning 24"));
    }

    @Test
    void test_containing_normal_comma_24_returns_true() throws InterruptedException {
        assertFalse(validate.valSimulation("normal,24"));
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

//    @Test
//    void test_valConfig_returns_true_when_config_is_correct() {
//        EController ec = new EController();
//        ec.maxFloor = 5;
//        ec.minFloor = 2;
//
//        assertTrue(validate.valConfig());
//    }

//    @Test
//    void test_config_correctly_formatted_returns_true() throws IOException {
//        File file = new File("config.json");
//        ValidatorOld v = new ValidatorOld();
//
//        Map<String, Integer> json = Runner.readFromJSONFile(file);
//        String elevator = Runner.MAPPER.writeValueAsString(json.get("elevator"));
//        String commands = Runner.MAPPER.writeValueAsString(json.get("commands"));
//
//        EController elevatorController = Runner.MAPPER.readValue(elevator, EController.class);
//
//        assertTrue(v.validateConfig(elevatorController));
//    }
    //Test depreciated
//    @Test
//    void test_config_incorrectly_formatted_returns_false() throws IOException {
//        File file = new File("config.json");
//        Validator v = new Validator();
//
//        Map<String, Integer> json = Runner.readFromJSONFile(file);
//        String elevator = Runner.MAPPER.writeValueAsString(json.get("elevator"));
//        String commands = Runner.MAPPER.writeValueAsString(json.get("commands"));
//
//        EController elevatorController = Runner.MAPPER.readValue(elevator, EController.class);
//
//        assertFalse(v.validateConfig(elevatorController));
//    }

}
