import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class GrammarTest {

    @Test
    public void eliminateEpsilon() {
        Set<Character> alphabet = new HashSet<>();
        alphabet.add('a');
        alphabet.add('b');
        Set<Character> nonTerminals = new HashSet<>();
        nonTerminals.add('S');
        nonTerminals.add('A');
        nonTerminals.add('B');
        Set<Production> productions = new HashSet<>();
        productions.add(new Production("S", "AB"));
        productions.add(new Production("A", "aAA"));
        productions.add(new Production("A", "B"));
        productions.add(new Production("B", "bBS"));
        productions.add(new Production("B", ""));
        Grammar g = new Grammar(alphabet, nonTerminals, productions, 'S');
        g.eliminateEpsilon();

        Set<Production> expected = new HashSet<>();
        expected.add(new Production("S", "AB"));
        expected.add(new Production("S", "A"));
        expected.add(new Production("S", "B"));
        expected.add(new Production("S", ""));
        expected.add(new Production("A", "aAA"));
        expected.add(new Production("A", "B"));
        expected.add(new Production("A", "aA"));
        expected.add(new Production("A", "a"));
        expected.add(new Production("B", "bBS"));
        expected.add(new Production("B", "bS"));
        expected.add(new Production("B", "b"));
        expected.add(new Production("B", "bB"));

        /*for(Production p : expected) {
            if(!g.productions.contains(p)) System.out.println(p);
        }
        for(Production p : g.productions) {
            if(!expected.contains(p)) System.out.println(p);
        }*/
        assertEquals(expected, g.productions);
    }

    @Test
    public void containsWord() {
        Set<Character> alphabet = new HashSet<>();
        alphabet.add('a');
        alphabet.add('b');
        Set<Character> nonTerminals = new HashSet<>();
        nonTerminals.add('S');
        Set<Production> productions = new HashSet<>();
        productions.add(new Production("S", ""));
        productions.add(new Production("S", "a"));
        productions.add(new Production("S", "b"));
        productions.add(new Production("S", "aSa"));
        productions.add(new Production("S", "bSb"));
        Grammar g = new Grammar(alphabet, nonTerminals, productions, 'S');

        assertTrue(g.containsWord(""));
        assertTrue(g.containsWord("a"));
        assertTrue(g.containsWord("aba"));
        assertTrue(g.containsWord("abba"));
        assertTrue(g.containsWord("bbb"));
        assertTrue(g.containsWord("babab"));
        assertFalse(g.containsWord("ab"));
        assertFalse(g.containsWord("bc"));
        assertFalse(g.containsWord("bba"));
        assertFalse(g.containsWord("bbaba"));
    }
}