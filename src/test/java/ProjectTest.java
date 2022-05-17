import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectTest {

    ValidatorOld validate;
    UserInputOld user;

    @Mock
    GenCommands gc = mock(GenCommands.class);;

    @BeforeEach
    void setUp(){

        validate = new ValidatorOld();
        user = new UserInputOld();

    }

    @Test
    void test_validate_returns_false_when_empty(){

        assertFalse(validate.validate(" "));

    }

    @Test
    void test_validate_returns_false_with_alphabetic_input(){
        assertFalse(validate.validate("a:b:c"));
    }

    @Test
    void test_validate_returns_false_with_mixed_input(){
        assertFalse(validate.validate("10:b:c"));
    }

    @Test
    void test_validate_returns_false_with_mixed_input_per_branch(){
        assertFalse(validate.validate("10a:b:c"));
    }

    @Test
    void test_validate_returns_true_with_10_2_8(){
        assertTrue(validate.validate("10:2:8"));
    }

    @Test
    void test_validate_returns_false_with_10_2_8_9(){
        assertFalse(validate.validate("10:2:8:9"));
    }

    @Test
    void test_validate_returns_false_with_10_2_8_colon(){
        assertFalse(validate.validate("10:2:8:"));
    }

    @Test
    void test_validate_returns_false_with_10_point_5_2_8(){
        assertFalse(validate.validate("10.5:2:8"));
    }

    @Test
    void test_validate_returns_false_with_2_10_point_5_8(){
        assertFalse(validate.validate("2:10.5:8"));
    }

    @Test
    void test_validate_returns_false_with_2_8_10_point_5(){
        assertFalse(validate.validate("2:8:10.5"));
    }

    @Test
    void test_validate_returns_false_with_2_8(){
        assertFalse(validate.validate("2:8"));
    }

    @Test
    void test_user_validate_returns_false_when_empty(){

        assertFalse(validate.validate(" "));

    }

    @Test
    void test_config_correctly_formatted_returns_true() throws IOException {
        File file = new File("config.json");
        ValidatorOld v = new ValidatorOld();

        Map<String, Integer> json = RunnerOld.readFromJSONFile(file);
        String elevator = RunnerOld.MAPPER.writeValueAsString(json.get("elevator"));
        String commands = RunnerOld.MAPPER.writeValueAsString(json.get("commands"));

        EController elevatorController = RunnerOld.MAPPER.readValue(elevator, EController.class);

        assertTrue(v.validateConfig(elevatorController));
    }
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
