import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Grammar {
    private static final Pattern TRANSITION_PATTERN = Pattern.compile(" -> *");
    private static final Pattern PRODUCTION_PATTERN = Pattern.compile(" +");

    public final Set<Terminal> alphabet;
    public final Set<NonTerminal> nonTerminals;
    public final Set<Production> productions;
    public final NonTerminal startSymbol;

    public Grammar(Set<Terminal> alphabet, Set<NonTerminal> nonTerminals, Set<Production> productions,
                   NonTerminal startSymbol) {
        this.alphabet = Collections.unmodifiableSet(new HashSet<>(Objects.requireNonNull(alphabet)));
        this.nonTerminals =
                Collections.unmodifiableSet(new HashSet<>(Objects.requireNonNull(nonTerminals)));
        this.productions =
                Collections.unmodifiableSet(new HashSet<>(Objects.requireNonNull(productions)));
        this.startSymbol = startSymbol;
        checkValidGrammar();
    }

    public void convertToChomsky() {
        addDirectNTs();
        decreaseProductionRightSize();
        eliminateEpsilon();
        eliminateChains();
    }

    public void addDirectNTs() {
        List<Character> newNTs = new ArrayList<>();
        List<Production> newProductions = new ArrayList<>();
        for (Production p : productions) {
            if (p.right.length() < 2) continue;
            StringBuilder newRight = new StringBuilder();
            for (char c : p.right.toCharArray()) {
                if (nonTerminals.contains(c)) {
                    newRight.append(c);
                } else {
                    Optional<Character> directNT = newProductions.stream().filter(prod -> prod.right.charAt(0) == c)
                            .map(prod -> prod.left.charAt(0)).findFirst();
                    if(directNT.isPresent()) {
                        newRight.append(directNT.get());
                    } else {
                        char nt = newNT;
                        newNT--;
                        newNTs.add(nt);
                        newProductions.add(new Production("" + nt, "" + c));
                        newRight.append(nt);
                    }
                }
            }
            p.right = newRight.toString();
        }
        nonTerminals.addAll(newNTs);
        productions.addAll(newProductions);
    }

    public void decreaseProductionRightSize() {
        List<Production> newProductions = new ArrayList<>();
        List<Production> toRemove = new ArrayList<>();
        for (Production p : productions) {
            if(p.right.length() <= 2) continue;
            toRemove.add(p);
            newProductions.addAll(decreaseRightSize(p));
        }
        productions.removeAll(toRemove);
        productions.addAll(newProductions);
    }

    private List<Production> decreaseRightSize(Production p) {
        List<Production> newProductions = new ArrayList<>();
        if(p.right.length() <= 2) {
            newProductions.add(p);
            return newProductions;
        }
        char nt = newNT;
        newNT--;
        nonTerminals.add(nt);
        newProductions.add(new Production(p.left, p.right.substring(0,1) + nt));
        newProductions.addAll(decreaseRightSize(new Production("" + nt, p.right.substring(1, p.right.length()))));
        return newProductions;
    }

    public void eliminateEpsilon() {
        Set<Character> epsilonNTs = findAllEpsilonNTs();
        Set<Production> newProductions = new HashSet<>();
        for (Production p : productions) {
            newProductions.addAll(p.getAllEpsilonCombinations(epsilonNTs));
        }
        productions.addAll(newProductions);
        productions.removeAll(productions.stream().filter(p -> p.isEpsilon() && p.left.charAt(0) != startingSymbol).collect(Collectors.toSet()));
        productions = new HashSet<>(productions); //Hack as the hashset somehow allowed duplicates
    }

    private Set<Character> findAllEpsilonNTs() {
        Set<Character> epsilonNTs = productions.stream().filter(Production::isEpsilon)
                .map(p -> p.left.charAt(0)).collect(Collectors.toSet());
        return findCharactersSatisfying(epsilonNTs, this::isComposedOfCharacters, true);
    }

    public void eliminateChains() {
        Set<Production> chainRules = findChainRules();
        productions.removeAll(chainRules);
        List<Production> toAdd = new ArrayList<>();
        for (Production chainRule : chainRules) {
            productions.stream().filter(p -> p.left.equals(chainRule.right)).forEach(p -> toAdd.add(new Production(chainRule.left, p.right)));
        }
        productions.addAll(toAdd);
    }

    public Set<Production> findChainRules() {
        Set<Production> chainRules = productions.stream().filter(p ->
                p.right.length() == 1 && nonTerminals.contains(p.right.charAt(0))).collect(Collectors.toSet());
        Set<Character> chainNTs = chainRules.stream().map(p -> p.left.charAt(0)).collect(Collectors.toSet());
        Set<Production> toAdd = new HashSet<>();
        do {
            chainRules.addAll(toAdd);
            toAdd.clear();
            for (Production p : productions) {
                if (p.right.length() == 1 && chainNTs.contains(p.right.charAt(0))) {
                    toAdd.addAll(chainRules.stream().filter(cr -> cr.left.equals(p.right))
                            .map(oldProduction -> new Production(p.left, oldProduction.right)).collect(Collectors.toSet()));
                }
            }
        } while(!chainRules.containsAll(toAdd));
        return chainRules;
    }

    private Set<Character> findCharactersSatisfying(Set<Character> base, BiPredicate<String, Set<Character>> condition, boolean addLeft) {
        Set<Character> toAdd = new HashSet<>();
        do {
            base.addAll(toAdd);
            toAdd.clear();
            for (Production p : productions) {
                if (addLeft) {
                    if (condition.test(p.right, base)) {
                        toAdd.add(p.left.charAt(0));
                    }
                } else {
                    if (condition.test(p.left, base)) {
                        for (char c : p.right.toCharArray()) toAdd.add(c);
                    }
                }
            }
        } while (!base.containsAll(toAdd));
        return base;
    }

    private boolean isComposedOfCharacters(String s, Set<Character> charSet) {
        for (char c : s.toCharArray()) {
            if (!charSet.contains(c)) return false;
        }
        return true;
    }

    public boolean isStartEpsilon() {
        return productions.stream().anyMatch(p -> p.left.charAt(0) == startingSymbol && p.right.equals(""));
    }

    static Grammar parse(Scanner scanner) {
        // First line
        String first = scanner.nextLine().trim();
        if (!first.equals("Grammar")) {
            throw new IllegalArgumentException("Parsed grammar does not start with 'Grammar'.");
        }

        // Second line: Non-Terminals
        String nonTerminalsString = scanner.nextLine();
        if (!nonTerminalsString.startsWith("Nonterminals:")) {
            throw new IllegalArgumentException("Parsed grammar does not declare Nonterminals first.");
        }
        nonTerminalsString = nonTerminalsString.substring("Nonterminals:".length()).trim();

        Set<NonTerminal> nonTerminals = new HashSet<>(NonTerminal.of(nonTerminalsString.split(",")));

        //Third line: Alphabet
        String alphabetString = scanner.nextLine();
        if (!alphabetString.startsWith("Alphabet:")) {
            throw new IllegalArgumentException("Parsed grammar does not declare Alphabet second.");
        }
        alphabetString = alphabetString.substring("Alphabet:".length()).trim();

        Set<Character> alphabetLetters = new HashSet<>();
        for (String terminal : alphabetString.split(",")) {
            if (terminal.length() == 1) {
                alphabetLetters.add(terminal.charAt(0));
            } else {
                throw new IllegalArgumentException("Alphabet has to be input as a comma separated list "
                        + "without spaces. Terminals may only be chars.");
            }
        }

        //Fourth line; Startsymbol
        String initialName = scanner.nextLine();
        if (!initialName.startsWith("Startsymbol:")) {
            throw new IllegalArgumentException("Parsed grammar does not declare start symbol third.");
        }
        initialName = initialName.substring("Startsymbol:".length()).trim();
        NonTerminal startSymbol = new NonTerminal(initialName);

        // Productions
        if (!scanner.nextLine().equals("Productions:")) {
            throw new IllegalArgumentException("Parsed grammar does not declare productions");
        }

        Set<Production> productions = new HashSet<>();
        String production;
        while (!(production = scanner.nextLine()).equals("END")) {
            if (!production.contains(" ->")) {
                throw new IllegalArgumentException("Production " + production + " does not contain ' ->'");
            }
            String[] split = TRANSITION_PATTERN.split(production);
            if (split.length > 2) {
                throw new IllegalArgumentException("Invalid production " + production);
            }

            List<NonTerminal> left = NonTerminal.of(split[0].split(" "));

            if (split.length == 1) { // "A -> "; empty production.
                productions.add(new Production(left, Collections.emptyList()));
            } else {
                String[] right = split[1].split("\\|");
                for (String r : right) {
                    r = r.trim();
                    String[] rightSideProductions = PRODUCTION_PATTERN.split(r);
                    List<Atom> atoms = new ArrayList<>(rightSideProductions.length);
                    for (String atom : rightSideProductions) {
                        atom = atom.trim();
                        if (atom.isEmpty()) {
                            atoms.add(new Terminal());
                        } else if (atom.length() == 1 && alphabetLetters.contains(atom.charAt(0))) {
                            atoms.add(new Terminal(atom.charAt(0)));
                        } else {
                            atoms.add(new NonTerminal(atom));
                        }
                    }
                    productions.add(new Production(left, atoms));
                }
                if (split[1].startsWith("|") || split[1].endsWith("|")) {
                    productions.add(new Production(left, new Terminal()));
                }
            }
        }

        Set<Terminal> alphabet = alphabetLetters.stream()
                .map(Terminal::new)
                .collect(Collectors.toSet());
        return new Grammar(alphabet, nonTerminals, productions, startSymbol);
    }

    @Override
    public String toString() {
        String productionsString = productions.isEmpty()
                ? "" : String.join("\n", Util.toString(new TreeSet<>(productions))) + "\n";
        return "Grammar\n"
                + "Nonterminals: " + String.join(",", Util.toString(new TreeSet<>(nonTerminals))) + "\n"
                + "Alphabet: " + String.join(",", Util.toString(new TreeSet<>(alphabet))) + "\n"
                + "Startsymbol: " + startSymbol + "\n"
                + "Productions:\n"
                + productionsString
                + "END";
    }

    private void checkValidGrammar() {
        for (Terminal t : alphabet) {
            for (NonTerminal nonTerminal : nonTerminals) {
                if (String.valueOf(t.letter()).equals(nonTerminal.name)) {
                    throw new IllegalArgumentException(
                            "Terminals and non-terminals must be disjoint! " + t + " is part of both.");
                }
            }
        }
        if (!nonTerminals.contains(startSymbol)) {
            throw new IllegalArgumentException(
                    "Starting symbol " + startSymbol + " must be part of the non-terminals");
        }

        Set<Atom> atoms = new HashSet<>(nonTerminals);
        atoms.addAll(alphabet);
        atoms.add(new Terminal()); // eps is always valid

        for (Production p : productions) {
            for (Atom atom : p.right) {
                if (!atoms.contains(atom)) {
                    throw new IllegalArgumentException(atom + " is not in the grammar");
                }
            }
        }
    }
}
