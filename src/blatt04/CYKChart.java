import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CYKChart {
    private Grammar grammar;
    private String word;
    private Map<Pair<Integer, Integer>, Set<Character>> chart = new HashMap<>();

    public CYKChart(Grammar grammar, String word) {
        this.grammar = grammar;
        this.word = word;
    }

    public boolean canProduce() {
        if(word.equals("")) return grammar.isStartEpsilon();
        return chart.get(new Pair<>(1, word.length())).stream().anyMatch(c -> c == grammar.startingSymbol);
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
            Set<Character> nts = grammar.productions.stream().filter(p -> p.right.equals("" + c))
                    .map(p -> p.left.charAt(0)).collect(Collectors.toSet());
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
        Set<Character> nts = new HashSet<>();
        //xx, x+1,y -> xx+1,x+2y
        for (int k = 0; k < j - i; k++) {
            Pair<Integer, Integer> bottomPair = new Pair<>(i+1, i+k+1);
            Pair<Integer, Integer> diagPair = new Pair<>(i+k+2,j+1);
            for (Character c1 : chart.get(bottomPair)) {
                for (Character c2 : chart.get(diagPair)) {
                    nts.addAll(grammar.productions.stream().filter(p -> p.right.equals("" + c1 + c2)).map(p -> p.left.charAt(0)).collect(Collectors.toSet()));
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
