import org.junit.Test;

public class CanonicDFATest {
    //@Test
    public void sample () {
        DFA d = MinimizeTest.loadFile("test_res/minimize_sample");
        System.out.println(CanonicDFA.canonicDFA(d));
    }

    //@Test
    public void d3 () {
        DFA d = MinimizeTest.loadFile("test_res/d3");
        System.out.println(CanonicDFA.canonicDFA(d));
    }
}

