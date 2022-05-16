import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserInputTest {

        UserInput validate;

        @BeforeEach
        void setUp(){

            validate = new UserInput();

        }

        @Test
        void test_user_validate_returns_false_when_empty(){

            assertFalse(validate.validate(" "));

        }

        @Test
        void test_user_validate_returns_false_with_alphabetic_input(){
            assertFalse(validate.validate("a:b:c"));
        }

        @Test
        void test_user_validate_returns_false_with_mixed_input(){
            assertFalse(validate.validate("10:b:c"));
        }

        @Test
        void test_user_validate_returns_false_with_mixed_input_per_branch(){
            assertFalse(validate.validate("10a:b:c"));
        }

        @Test
        void test_user_validate_returns_true_with_10_2_8(){
            assertTrue(validate.validate("10:2:8"));
        }

        @Test
        void test_user_validate_returns_false_with_10_2_8_9(){
            assertFalse(validate.validate("10:2:8:9"));
        }

        @Test
        void test_user_validate_returns_false_with_10_2_8_colon(){
            assertFalse(validate.validate("10:2:8:"));
        }

        @Test
        void test_user_validate_returns_false_with_10_point_5_2_8(){
            assertFalse(validate.validate("10.5:2:8"));
        }

        @Test
        void test_user_validate_returns_false_with_2_10_point_5_8(){
            assertFalse(validate.validate("2:10.5:8"));
        }

        @Test
        void test_user_validate_returns_false_with_2_8_10_point_5(){
            assertFalse(validate.validate("2:8:10.5"));
        }

}
