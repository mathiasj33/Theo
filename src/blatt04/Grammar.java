import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    /**
     * Parses the grammar that is defined in the file f.
     * A file may look like this
     * Grammar
     * Nonterminals: A,B,C
     * Alphabet: a,b,c
     * Startsymbol: A
     * Productions:
     * A -> Aa
     * A -> AB
     * Needs to start with "Grammar"
     * Second line: "Nonterminals: " and then all nonterminals, which can only be chars, in a comma separated list
     * without spaces.
     * Third line: "Alphabet: " and then all terminals, see above.
     * Fourth line: "Startsymbol: " and then the startsymbol
     * Fifth line: "Productions:"
     * Then each production takes a line; left and right side are separated by " -> "; using multiple right sides with
     * "|" is currently not supported.
     */
    static Grammar parse(Scanner scanner) {
        //First line
        String first = scanner.nextLine();
        if (!first.equals("Grammar")) {
            throw new IllegalArgumentException("Parsed grammar does not start with 'Grammar'.");
        }

        //Second line; nonterminals
        String second = scanner.nextLine();
        if (!second.startsWith("Nonterminals: ")) {
            throw new IllegalArgumentException("Parsed grammar does not declare Nonterminals first.");
        }
        Set<Character> nonterminals = new HashSet<>();
        second = second.substring("Nonterminals: ".length());
        for (String nonterminal : second.split(",")) {
            if (nonterminal.length() == 1) {
                nonterminals.add(nonterminal.charAt(0));
            } else {
                throw new IllegalArgumentException(
                        "Nonterminals have to be input as a comma separated list without spaces. Nonterminals may only be chars.");
            }
        }

        //Third line; Alphabet
        String third = scanner.nextLine();
        if (!third.startsWith("Alphabet: ")) {
            throw new IllegalArgumentException("Parsed grammar does not declare Alphabet second.");
        }
        Set<Character> alphabet = new HashSet<>();
        third = third.substring("Alphabet: ".length());
        for (String terminal : third.split(",")) {
            if (terminal.length() == 1) {
                alphabet.add(terminal.charAt(0));
            } else {
                throw new IllegalArgumentException(
                        "Alphabet has to be input as a comma separated list without spaces. Terminals may only be chars.");
            }
        }

        //Fourth line; Startsymbol
        String fourth = scanner.nextLine();
        if (!fourth.startsWith("Startsymbol: ")) {
            throw new IllegalArgumentException("Parsed grammar does not declare start symbol third.");
        }
        fourth = fourth.substring("Startsymbol: ".length());
        char start;
        if (fourth.length() == 1) {
            start = fourth.charAt(0);
        } else {
            throw new IllegalArgumentException("Startsymbol must be a single char.");
        }

        //Fifth line; rules
        String fifth = scanner.nextLine();
        if (!fifth.equals("Productions:")) {
            throw new IllegalArgumentException(
                    "Parsed grammar does not contain the String 'Productions' in the fifth line.");
        }
        Set<Production> productions = new HashSet<>();
        String production;
        while (!(production = scanner.nextLine()).equals("END")) {
            if (!production.contains(" -> ")) {
                throw new IllegalArgumentException("Production " + production + " does not contain ' -> '");
            }
            String[] split = production.split(" -> ");
            String left = split[0];
            if (split.length == 1) { // "A -> "; empty production.
                productions.add(new Production(left, ""));
            } else {
                String[] right = split[1].split("\\|");
                for (String r : right) {
                    productions.add(new Production(left, r));
                }
                int j = split[1].lastIndexOf("|");
                if (split[1].lastIndexOf("|") == split[1].length() - 1) {
                    productions.add(new Production(left, ""));
                }
            }
        }

        return new Grammar(alphabet, nonterminals, productions, start);
    }

    final Set<Character> alphabet;
    final Set<Character> nonTerminals;
    final Set<Production> productions;
    final char startingSymbol;

    public Grammar(Set<Character> alphabet, Set<Character> nonTerminals, Set<Production> productions,
                   char startingSymbol) {
        this.alphabet = Collections.unmodifiableSet(new HashSet<>(alphabet));
        this.nonTerminals = Collections.unmodifiableSet(new HashSet<>(nonTerminals));
        this.productions = new HashSet<>(productions);
        this.startingSymbol = startingSymbol;
        checkValidGrammar();
    }

    public void eliminateEpsilon() {
        Set<Character> epsilonNTs = findAllEpsilonNTs();
        Set<Production> newProductions = new HashSet<>();
        for (Production p : productions) {
            newProductions.addAll(p.getAllEpsilonCombinations(epsilonNTs));
        }
        productions.addAll(newProductions);
        productions.removeAll(productions.stream().filter(p -> p.isEpsilon() && p.left.charAt(0) != startingSymbol).collect(Collectors.toSet()));
    }

    private Set<Character> findAllEpsilonNTs() {
        Set<Character> epsilonNTs = productions.stream().filter(Production::isEpsilon)
                .map(p -> p.left.charAt(0)).collect(Collectors.toSet());
        Set<Character> newEpsilonNTs = new HashSet<>();
        do {
            epsilonNTs.addAll(newEpsilonNTs);
            newEpsilonNTs.clear();
            for (Production p : productions) {
                if (isComposedOfCharacters(p.right, epsilonNTs)) {
                    newEpsilonNTs.add(p.left.charAt(0));
                }
            }
        } while (!epsilonNTs.containsAll(newEpsilonNTs));
        return epsilonNTs;
    }

    private boolean isComposedOfCharacters(String s, Set<Character> charSet) {
        for (char c : s.toCharArray()) {
            if (!charSet.contains(c)) return false;
        }
        return true;
    }

    public boolean containsWord(String word) {
        List<String> level = new ArrayList<>();
        level.add("" + startingSymbol);
        while (true) {
            level = generateNextLevel(level);
            //TODO
        }
    }

    private List<String> generateNextLevel(List<String> level) {
        List<String> nextLevel = new ArrayList<>();
        for (String s : level) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (!nonTerminals.contains(c)) continue;
                for (String expanded : expandNonTerminal(c)) {
                    nextLevel.add(s.substring(0, i) + expanded + s.substring(i + 1, s.length()));
                }
            }
        }
        return nextLevel;
    }

    private List<String> expandNonTerminal(char nt) {
        List<String> result = new ArrayList<>();
        for (Production production : productions) {
            if (production.left.charAt(0) == nt) result.add(production.right);
        }
        return result;
    }

    @Override
    public String toString() {
        return "Grammar{" + "alphabet=" + alphabet + ", nonTerminals=" + nonTerminals + ", productions=" + productions
                + ", startingSymbol=" + startingSymbol + '}';
    }

    private void checkValidGrammar() {
        if (alphabet == null) {
            throw new IllegalArgumentException("Grammar constructor: alphabet cannot be null.");
        }
        if (nonTerminals == null) {
            throw new IllegalArgumentException("Grammar constructor: nonTerminals cannot be null.");
        }
        if (productions == null) {
            throw new IllegalArgumentException("Grammar constructor: productions cannot be null.");
        }
        for (Character t : alphabet) {
            if (nonTerminals.contains(t)) {
                throw new IllegalArgumentException("Terminals and nonterminals must be disjoint! " + t + " is part of both.");
            }
        }
        if (!nonTerminals.contains(startingSymbol)) {
            throw new IllegalArgumentException("Starting symbol " + startingSymbol + " must be part of the nonterminals");
        }
        for (Production p : productions) {
            //no need to check that p is not null, because productions is a valid set, and
            // hence only valid productions can be in it
            if (p.left.isEmpty()) {
                throw new IllegalArgumentException("Production left side must be set for production " + p);
            }

            //if right side has length 0, then it is epsilon, which is fine
            char[] letters = new char[p.left.length() + p.right.length()];
            p.left.getChars(0, p.left.length(), letters, 0);
            p.right.getChars(0, p.right.length(), letters, p.left.length());
            for (char c : letters) {
                if (!alphabet.contains(c) && !nonTerminals.contains(c)) {
                    throw new IllegalArgumentException(
                            "Letter " + c + " in production " + p + " is neither a terminal nor a nonterminal");
                }
            }
        }
    }
}