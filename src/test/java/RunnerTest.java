import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RunnerTest {

    Validator validate;

    @Mock
    Runner r = mock(Runner.class);

    @Mock
    File file;

    @Mock
    Thread a;




    @BeforeEach
    void setUp(){

        validate = new Validator();
    }

    @Test
    void test_readFromJSONFILE_returns_map() throws InterruptedException, IOException {
        Map<String, Integer> hm = new HashMap<>();
        hm.put("", 1);

        when(r.readFromJSONFile(file)).thenReturn(hm);

        assertEquals(hm, r.readFromJSONFile(file));
        verify(r, times(1)).readFromJSONFile(file);

    }

    @Test
    void test_StopThreads_prints_once() throws InterruptedException {
        r.stopThreads(a, a);

        verify(r, times(1)).stopThreads(a,a);
    }

    //@Test




}
