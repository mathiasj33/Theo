import java.util.*;
import java.util.stream.Collectors;

public class DFA extends NFA {

    private Map<UnorderedPair<State>, Set<UnorderedPair<State>>> inverseMapping;
    private Set<UnorderedPair<State>> distinctPairs;

    public DFA(Set<State> states, Set<Transition> transitions, Set<Character> alphabet, State startState, Set<State> finalStates) {
        super(states, transitions, alphabet, startState, finalStates);
        inverseMapping = new HashMap<>();
        distinctPairs = new HashSet<>();
        checkValidDFA();
    }

    public void minimize() {
        removeNonReachableStates();
        fillInverseMapping();
        markDistinctPairs();
        collapse();
    }

    private void removeNonReachableStates() {
        Set<State> result = new HashSet<>();
        Stack<State> workingList = new Stack<>();
        workingList.push(startState);
        while (!workingList.isEmpty()) {
            State current = workingList.pop();
            result.add(current);
            getAllPossibleSuccessors(current).stream().filter(s -> !result.contains(s)).forEach(workingList::push);
        }

        List<State> toRemove = new ArrayList<>();
        states.stream().filter(s -> !result.contains(s)).forEach(toRemove::add);

        List<Transition> transitionsToRemove = new ArrayList<>();

        toRemove.forEach(s -> {
            states.remove(s);
            finalStates.remove(s);
            List<Transition> sTransitions = transitions.stream().filter(t -> t.getStart().equals(s) || t.getEnd().equals(s)).collect(Collectors.toList());
            transitionsToRemove.addAll(sTransitions);
        });

        transitions.removeAll(transitionsToRemove);
    }

    private void fillInverseMapping() {
        for (State s1 : states) {
            for (State s2 : states) {
                if (s1.equals(s2)) continue;
                for (Character c : alphabet) {
                    State r1 = getSuccessor(s1, c);
                    State r2 = getSuccessor(s2, c);
                    if (r1.equals(r2)) continue;
                    inverseMapping.putIfAbsent(new UnorderedPair<>(r1, r2), new HashSet<>());
                    Set<UnorderedPair<State>> set = inverseMapping.get(new UnorderedPair<>(r1, r2));
                    set.add(new UnorderedPair<>(s1, s2));
                }
            }
        }
    }

    private void markDistinctPairs() {
        states.stream().filter(finalStates::contains).forEach(f ->
                states.stream().filter(s -> !finalStates.contains(s)).forEach(nf -> {
                    if (!f.equals(nf)) {
                        markPairWithPredecessors(new UnorderedPair<>(f, nf));
                    }
                }));
    }

    private void markPairWithPredecessors(UnorderedPair<State> pair) {
        if (distinctPairs.contains(pair)) return;
        distinctPairs.add(pair);
        if (inverseMapping.get(pair) == null) return;
        inverseMapping.get(pair).forEach(this::markPairWithPredecessors);
    }

    private void collapse() {
        List<State> toRemove = new ArrayList<>();
        for (State s1 : states) {
            if (toRemove.contains(s1)) continue;
            for (State s2 : states) {
                if (s2.equals(s1)) continue;
                if (toRemove.contains(s2)) continue;
                if (distinctPairs.contains(new UnorderedPair<>(s1, s2))) continue;
                combineStates(s1, s2);
                toRemove.add(s2);
                if (startState.equals(s2)) startState = s1;
            }
        }

        states.removeAll(toRemove);
        finalStates.removeAll(toRemove);
    }

    private void combineStates(State s1, State s2) {
        //collapses s2 into s1
        for (Transition t : transitions) {
            if (t.getStart().equals(s2)) {
                t.setStart(s1);
            }
            if (t.getEnd().equals(s2)) {
                t.setEnd(s1);
            }
        }
    }

    public void renameToCanonic() {
        states.forEach(s -> s.setName("unnamed"));
        Set<State> named = new HashSet<>();
        Queue<Pair<State, String>> workingList = new LinkedList<>();
        workingList.add(new Pair<>(startState, ""));
        while (!workingList.isEmpty()) {
            Pair<State, String> current = workingList.remove();
            String name = current.b.equals("") ? "eps" : current.b;
            current.a.setName(name);
            named.add(current.a);

            for (Transition t : transitions) {
                if(!t.getStart().equals(current.a)) continue;
                String newName = current.b + t.getLabel();
                if (named.contains(t.getEnd())) {
                    String endName = t.getEnd().getName();
                    if(endName.equals("eps")) endName = "";
                    if(endName.length() < newName.length()) continue;
                    if(endName.compareTo(newName) < 0) continue;
                    t.getEnd().setName(newName);
                }
                else if(!named.contains(t.getEnd())){
                    workingList.add(new Pair<>(t.getEnd(), newName));
                }
            }
        }
    }

    /**
     * Returns the successor of a state-letter pair.
     * It surely exists, because the transition relation was checked to be total in checkValidDFA.
     * There surely is only one, because that was also checked in checkValidDFA
     * If the given char is not in the alphabet, returns null
     *
     * @param s The state from which to start.
     * @param a The letter to read
     * @return The successor of the transition starting in s reading a.
     */
    public State getSuccessor(State s, char a) {
        for (Transition t : transitions) {
            if (t.getStart().equals(s) && t.getLabel() == a) {
                return t.getEnd();
            }
        }
        return null;
    }


    /**
     * Checks whether the given parameters indeed describe a DFA.
     * Throws an exception describing the problem if some parameter is invalid.
     * Checks firstly that each state has a transition for each label at most once, after that at least once.
     * We do not need to check that the transition labels are a subset of the alphabet, that is done in checkValidEpsilonNFA
     */
    private void checkValidDFA() throws IllegalArgumentException {
        for (State s : states) {
            Set<Character> transLabels = new HashSet<>();
            for (Transition t : getTransitionsFromState(s)) {
                if (!transLabels.add(t.getLabel())) {
                    throw new IllegalArgumentException("State " + s + " has more than one transition for label " + t.getLabel());
                }
            }
            if (!transLabels.containsAll(alphabet)) {
                Set<Character> missingSet = new HashSet<>(alphabet);
                missingSet.removeAll(transLabels);
                throw new IllegalArgumentException("State " + s + " is missing a transition for label(s): " + missingSet);
            }
        }
    }

    @Override
    public String toString() {
        return "DFA\n" + toStringHelper();
    }

}