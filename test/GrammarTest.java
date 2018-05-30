import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static org.junit.Assert.*;

public class GrammarTest {

    private static final String CHOMSKY_GRAMMAR = "test_res/chomskyExample.txt";

    private Grammar loadGrammar(String path) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(new File(path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("Couldn't find grammar");
        }
        return Grammar.parse(scanner);
    }

    @Test
    public void findGenerating() {
        Grammar g = loadGrammar(CHOMSKY_GRAMMAR);
        Set<Character> expected = new HashSet<>();
        expected.add('A');
        expected.add('B');
        expected.add('D');
        expected.add('S');
        assertEquals(expected, g.findGeneratingNTs());
    }

    @Test
    public void eliminateNonGenerating() {
        Grammar g = loadGrammar(CHOMSKY_GRAMMAR);
        Set<Production> expectedProductions = new HashSet<>(g.productions);
        expectedProductions.remove(new Production("C", "aC"));
        expectedProductions.remove(new Production("A", "CB"));
        expectedProductions.remove(new Production("D", "aSCb"));
        Set<Character> expectedNTs = new HashSet<>(g.nonTerminals);
        expectedNTs.remove('C');

        g.eliminateNonGeneratingNTs();
        assertEquals(expectedNTs, g.nonTerminals);
        assertEquals(expectedProductions, g.productions);
    }

    @Test
    public void findReachable() {
        Grammar g = loadGrammar(CHOMSKY_GRAMMAR);
        g.eliminateNonGeneratingNTs();
        Set<Character> expected = new HashSet<>();
        expected.add('S');
        expected.add('A');
        expected.add('B');
        assertEquals(expected, g.findReachableNTs());
    }

    @Test
    public void eliminateNonReachable() {
        Grammar g = loadGrammar(CHOMSKY_GRAMMAR);
        g.eliminateNonGeneratingNTs();
        Set<Production> expectedProductions = new HashSet<>(g.productions);
        expectedProductions.remove(new Production("D", "a"));
        Set<Character> expectedNTs = new HashSet<>(g.nonTerminals);
        expectedNTs.remove('D');

        g.eliminateNonReachableNTs();
        assertEquals(expectedNTs, g.nonTerminals);
        assertEquals(expectedProductions, g.productions);
    }

    @Test
    public void addCNFNTs() {

    }

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

    //@Test
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