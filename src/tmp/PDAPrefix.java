package tmp;

import java.util.*;

public class PDAPrefix {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();//throw away number of lines
        PDA m = PDAParser.parse(scanner);
        System.out.println("Case #1:\n" + pdaPrefix(m));
        scanner.close();
    }

    public static PDA pdaPrefix(PDA m) {
        Map<State, State> toEpsilonState = new HashMap<>();
        m.getStates().forEach(s -> toEpsilonState.put(s, new State(s.getName() + "_eps")));
        Set<PDATransition> epsilonTransitions = new HashSet<>();
        m.getTransitions().forEach(t -> epsilonTransitions.add(new PDATransition(toEpsilonState.get(t.start),
                Util.EPSILON, t.popSymbol, toEpsilonState.get(t.end), t.pushSymbols)));
        for (State s : m.getStates()) {
            for (Character c : m.getStackAlphabet()) {
                epsilonTransitions.add(new PDATransition(s, Util.EPSILON, c, toEpsilonState.get(s), ""+c));
            }
        }
        Set<State> allStates = new HashSet<>();
        allStates.addAll(m.getStates());
        allStates.addAll(toEpsilonState.values());
        Set<PDATransition> allTransitions = new HashSet<>();
        allTransitions.addAll(m.getTransitions());
        allTransitions.addAll(epsilonTransitions);
        return new PDA(allStates, allTransitions, m.getAlphabet(), m.getStartState(), m.getStackAlphabet(), m.getStartSymbol());
    }
}
