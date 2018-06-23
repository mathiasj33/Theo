import org.junit.Test;

import static org.junit.Assert.*;

public class ToEpsilonTest {

    private static final String PATH = "src/blatt06/test/";

    @Test
    public void toEpsilonNFA() {
        TuringMachine<String> tm = TestUtils.loadTM(PATH + "sample3.1.txt");
        EpsilonNFA nfa = ToEpsilon.toEpsilonNFA(tm);
        System.out.println(nfa);
    }
}