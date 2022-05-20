//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class UserInputOldTest {
//
//    @Mock
//    UserInput userInput = mock(UserInput.class);
//
//    @Mock
//    GenCommands gc = mock(GenCommands.class);
//
//    @Mock
//    Thread commandGen = mock(Thread.class);
//
//    Validator v;
//
//    @BeforeEach
//    void setUp() {
//        v = new Validator();
//    }
//
//    @Test
//    void test_user_validate_returns_false_when_empty() throws InterruptedException {
//
//        when(userInput.userInput(commandGen, gc)).thenReturn(false);
//
//        assertFalse(userInput.userInput(commandGen, gc);
//        verify(v, times(1))).valInput("");
//    }
//
//    @Test
//    void test_user_validate_returns_false_with_alphabetic_input() {
//        assertFalse(userInput.validate("a:b:c"));
//    }
//
//    @Test
//    void test_user_validate_returns_false_with_mixed_input() {
//        assertFalse(userInput.validate("10:b:c"));
//    }
//
//    @Test
//    void test_user_validate_returns_false_with_mixed_input_per_branch() {
//        assertFalse(userInput.validate("10a:b:c"));
//    }
//
//    @Test
//    void test_user_validate_returns_true_with_10_2_8() {
//        assertTrue(userInput.validate("10:2:8"));
//    }
//
//    @Test
//    void test_user_validate_returns_false_with_10_2_8_9() {
//        assertFalse(userInput.validate("10:2:8:9"));
//    }
//
//    @Test
//    void test_user_validate_returns_false_with_10_2_8_colon() {
//        assertFalse(userInput.validate("10:2:8:"));
//    }
//
//    @Test
//    void test_user_validate_returns_false_with_10_point_5_2_8() {
//        assertFalse(userInput.validate("10.5:2:8"));
//    }
//
//    @Test
//    void test_user_validate_returns_false_with_2_10_point_5_8() {
//        assertFalse(userInput.validate("2:10.5:8"));
//    }
//
//    @Test
//    void test_user_validate_returns_false_with_2_8_10_point_5() {
//        assertFalse(userInput.validate("2:8:10.5"));
//    }
//}
