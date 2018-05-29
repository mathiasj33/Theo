import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ProductionTest {

    @Test
    public void getAllEpsilonCombinations() {
        Production p = new Production("A", "aAA");
        Set<Character> epsilonNTs = new HashSet<>();
        epsilonNTs.add('A');
        Set<Production> expected = new HashSet<>();
        Production p1 = new Production("A", "aA");
        Production p2 = new Production("A", "a");
        Production p3 = new Production("A", "aAA");
        expected.add(p1);
        expected.add(p2);
        expected.add(p3);
        assertEquals(expected, p.getAllEpsilonCombinations(epsilonNTs));

        epsilonNTs.add('B');
        expected.clear();
        p1 = new Production("B", "bS");
        p2 = new Production("B", "bBS");
        p3 = new Production("B", "bAS");
        Production p4 = new Production("B", "bABS");
        expected.add(p1);
        expected.add(p2);
        expected.add(p3);
        expected.add(p4);
        assertEquals(expected, new Production("B", "bABS").getAllEpsilonCombinations(epsilonNTs));
    }

    @Test
    public void powerSet() {
        Set<Character> chars = new HashSet<>();
        chars.add('a');
        chars.add('b');
        chars.add('c');

        Set<Set<Character>> expected = new HashSet<>();
        Set<Character> a = new HashSet<>();
        Set<Character> b = new HashSet<>();
        Set<Character> c = new HashSet<>();
        Set<Character> ab = new HashSet<>();
        Set<Character> ac = new HashSet<>();
        Set<Character> bc = new HashSet<>();
        Set<Character> abc = new HashSet<>();
        Set<Character> empty = new HashSet<>();
        a.add('a');
        b.add('b');
        c.add('c');
        ab.add('a');
        ab.add('b');
        ac.add('a');
        ac.add('c');
        bc.add('b');
        bc.add('c');
        abc.add('a');
        abc.add('b');
        abc.add('c');
        expected.add(a);
        expected.add(b);
        expected.add(c);
        expected.add(ab);
        expected.add(ac);
        expected.add(bc);
        expected.add(abc);
        expected.add(empty);

        Set<Set<Character>> actual = new HashSet<>(Production.powerSet(new ArrayList<>(chars)));
        assertEquals(expected, actual);
    }
}