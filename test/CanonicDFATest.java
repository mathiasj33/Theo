import org.junit.Test;

public class CanonicDFATest {
    @Test
    public void sample () {
        DFA d = MinimizeTest.loadFile("minimize_sample");
        //System.out.println(CanonicDFA.canonicDFA(d));
    }

    @Test
    public void d3 () {
        DFA d = MinimizeTest.loadFile("d3");
        //System.out.println(CanonicDFA.canonicDFA(d));
    }
}

