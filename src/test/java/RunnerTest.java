import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
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


    @Mock
    FrameView fm;

    @Mock
    EController eController;

    @Mock
    Elevator elevator;

    @Mock
    ObjectMapper MAPPER;


    Runner runner;
    @BeforeEach
    void setUp(){

        validate = new Validator();
        runner = new Runner();
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

    @Test
    void test_StartThreads_closes_graphics() throws InterruptedException, IOException {

        when(r.createObjects(file)).then(r.readFromJSONFile(file));

        verify(r, times(1)).readFromJSONFile(file);
    }




}
