package tmp2;

import java.util.*;
import java.util.stream.Collectors;

public class CYKChart {
    private Grammar grammar;
    public String word;
    public Map<Pair<Integer, Integer>, Set<NonTerminal>> chart = new HashMap<>();

    public CYKChart(Grammar grammar, String word) {
        this.grammar = grammar;
        this.word = word;
    }

    public void fillChart() {
        fillBasis();
        fillRest();
    }

    private void fillBasis() {
        for (int i = 0; i < word.length(); i++) {
            Pair<Integer, Integer> pair = new Pair<>(i+1, i+1);
            chart.putIfAbsent(pair, new HashSet<>());
            char c = word.charAt(i);
            Set<NonTerminal> nts = grammar.productions.stream().filter(p -> p.right.size() == 1 && p.right.get(0).equals(new Terminal(c)))
                    .map(p -> p.left.get(0)).collect(Collectors.toSet());
            chart.get(pair).addAll(nts);
        }
    }

    private void fillRest() {
        for (int diff = 1; diff < word.length(); diff++) {
            for (int i = 0; i < word.length() - diff; i++) {
                completeChartField(i, i+diff);
            }
        }
    }

    private void completeChartField(int i, int j) {
        Pair<Integer, Integer> pair = new Pair<>(i + 1, j + 1);
        Set<NonTerminal> nts = new HashSet<>();
        //xx, x+1,y -> xx+1,x+2y
        for (int k = 0; k < j - i; k++) {
            Pair<Integer, Integer> bottomPair = new Pair<>(i+1, i+k+1);
            Pair<Integer, Integer> diagPair = new Pair<>(i+k+2,j+1);
            for (Atom c1 : chart.get(bottomPair)) {
                for (Atom c2 : chart.get(diagPair)) {
                    List<Atom> rightSide = new ArrayList<>();
                    rightSide.add(c1);
                    rightSide.add(c2);
                    nts.addAll(grammar.productions.stream().filter(p -> p.right.equals(rightSide)).map(p -> p.left.get(0)).collect(Collectors.toSet()));
                }
            }
        }
        chart.put(pair, nts);
    }

    @Override
    public String toString() {
        return "CYKChart{" +
                "chart=" + chart +
                '}';
    }
}
