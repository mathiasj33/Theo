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
    public void addDirectNTs() {
        Grammar g = loadGrammar(CHOMSKY_GRAMMAR);
        g.clean();
        Set<Production> expectedProductions = new HashSet<>(g.productions);
        expectedProductions.remove(new Production("S", "aB"));
        expectedProductions.add(new Production("S", '\uffff' + "B"));
        expectedProductions.add(new Production("" + '\uffff', "a"));

        Set<Character> expectedNTs = new HashSet<>(g.nonTerminals);
        expectedNTs.add('\uffff');

        g.addDirectNTs();
        assertEquals(expectedNTs, g.nonTerminals);
        assertEquals(expectedProductions, g.productions);
    }

    @Test
    public void decreaseProductionRightSize() {
        Grammar g = loadGrammar(CHOMSKY_GRAMMAR);
        g.clean();
        g.addDirectNTs();

        Set<Production> expectedProductions = new HashSet<>(g.productions);
        expectedProductions.remove(new Production("S", "ASA"));
        expectedProductions.add(new Production("S","A" + (char) ('\uffff' - 1)));
        expectedProductions.add(new Production("" + (char) ('\uffff' -1), "SA"));

        Set<Character> expectedNTs = new HashSet<>(g.nonTerminals);
        expectedNTs.add((char) ('\uffff' -1));

        g.decreaseProductionRightSize();
        assertEquals(expectedNTs, g.nonTerminals);
        assertEquals(expectedProductions, g.productions);
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

        Set<Production> expectedProductions = new HashSet<>();
        expectedProductions.add(new Production("S", "AB"));
        expectedProductions.add(new Production("S", "A"));
        expectedProductions.add(new Production("S", "B"));
        expectedProductions.add(new Production("S", ""));
        expectedProductions.add(new Production("A", "aAA"));
        expectedProductions.add(new Production("A", "B"));
        expectedProductions.add(new Production("A", "aA"));
        expectedProductions.add(new Production("A", "a"));
        expectedProductions.add(new Production("B", "bBS"));
        expectedProductions.add(new Production("B", "bS"));
        expectedProductions.add(new Production("B", "b"));
        expectedProductions.add(new Production("B", "bB"));

        assertEquals(expectedProductions, g.productions);

        g = loadGrammar(CHOMSKY_GRAMMAR);
        g.clean();
        g.addDirectNTs();
        g.decreaseProductionRightSize();

        expectedProductions = new HashSet<>(g.productions);
        char xa = '\uffff';
        char xsa = (char) (xa - 1);
        expectedProductions.add(new Production("S", ""+xsa));
        expectedProductions.add(new Production("S", ""+xa));
        expectedProductions.remove(new Production("B", ""));
        expectedProductions.add(new Production("" + xsa, "S"));

        g.eliminateEpsilon();

        assertEquals(expectedProductions, g.productions);
    }

    @Test
    public void findChainRules() {
        Grammar g = loadGrammar(CHOMSKY_GRAMMAR);
        g.clean();
        g.addDirectNTs();
        g.decreaseProductionRightSize();
        g.eliminateEpsilon();

        Set<Production> expectedChainRules = new HashSet<>();
        char xa = '\uffff';
        char xsa = (char) (xa - 1);
        expectedChainRules.add(new Production("S", "" + xsa));
        expectedChainRules.add(new Production("S", "" + xa));
        expectedChainRules.add(new Production("A", "B"));
        expectedChainRules.add(new Production("A", "S"));
        expectedChainRules.add(new Production("" + xsa, "S"));

        expectedChainRules.add(new Production("S", "S"));
        expectedChainRules.add(new Production("A", "" + xsa));
        expectedChainRules.add(new Production("A", "" + xa));
        expectedChainRules.add(new Production(""+xsa, "" + xsa));
        expectedChainRules.add(new Production(""+xsa, "" + xa));

        assertEquals(expectedChainRules, g.findChainRules());
    }

    @Test
    public void eliminateChains() {
        Grammar g = loadGrammar(CHOMSKY_GRAMMAR);
        g.clean();
        g.addDirectNTs();
        g.decreaseProductionRightSize();
        g.eliminateEpsilon();

        Set<Production> expectedProductions = new HashSet<>(g.productions);
        char xa = '\uffff';
        char xsa = (char) (xa - 1);
        expectedProductions.remove(new Production("S",""+xsa));
        expectedProductions.remove(new Production("S",""+xa));
        expectedProductions.add(new Production("S", "SA"));
        expectedProductions.add(new Production("S", "a"));
        expectedProductions.remove(new Production("A", "B"));
        expectedProductions.remove(new Production("A", "S"));
        expectedProductions.add(new Production("A", "b"));
        expectedProductions.add(new Production("A", "A"+xsa));
        expectedProductions.add(new Production("A", "SA"));
        expectedProductions.add(new Production("A", xa+"B"));
        expectedProductions.add(new Production("A", "a"));
        expectedProductions.remove(new Production("" + xsa, "S"));
        expectedProductions.add(new Production("" + xsa, "A"+xsa));
        expectedProductions.add(new Production("" + xsa, xa+"B"));
        expectedProductions.add(new Production("" + xsa, "a"));

        g.eliminateChains();

        assertEquals(expectedProductions, g.productions);
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