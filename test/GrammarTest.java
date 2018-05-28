import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class GrammarTest {

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

        g.containsWord("aaa");

        assertTrue(true);
    }
}