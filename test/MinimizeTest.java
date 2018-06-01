import org.junit.Test;

import java.io.File;
import java.util.Scanner;

public class MinimizeTest {
    public static DFA loadFile(String filename) {
        EpsilonNFA e;
        String file = filename;

        try (Scanner s = new Scanner(new File(file))) {
            s.nextLine();//remove the number of lines in the beginning
            e = Parser.parse(s);
            DFA d1 = null;
            if (e instanceof DFA) {
                d1 = (DFA) e;
                return d1;
            } else {
                System.out.println("First automaton is no week_5.DFA, aborting");
                System.exit(3);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ops");
            return null;
        }
        return null;
    }

    @Test
    public void sample () {
        DFA d = loadFile("test_res/minimize_sample");
        System.out.println(Minimize.minimize(d));
    }

    @Test
    public void d3 () {
        DFA d = loadFile("test_res/d3");
        System.out.println(Minimize.minimize(d));
    }
}

