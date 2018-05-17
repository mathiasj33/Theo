import java.util.*;
import java.util.stream.Collectors;

public class DFA extends NFA {

    public DFA(Set<State> states, Set<Transition> transitions, Set<Character> alphabet, State startState, Set<State> finalStates) {
        super(states, transitions, alphabet, startState, finalStates);
        checkValidDFA();
    }

    public Set<State> getReachableStates(Set<State> k) {
        Set<State> result = new HashSet<>();
        List<State> workingList = new ArrayList<>();
        workingList.addAll(k);
        while(!workingList.isEmpty()) {
            State current = workingList.remove(0);
            if (!result.contains(current)) {
                result.add(current);
                for (State s : getAllPossibleSuccessors(current)) {
                    if(!workingList.contains(s)) workingList.add(s);
                }
            }
        }
        return result;
    }

    public boolean isFinite() {
        Set<State> startSet = new HashSet<>();
        startSet.add(startState);
        Set<State> reachable = getReachableStates(startSet);
        Set<State> loops = reachable.stream().filter(
                s -> getReachableStates(getAllPossibleSuccessors(s)).contains(s)
        ).collect(Collectors.toSet());
        return getReachableStates(loops).stream().noneMatch(s -> finalStates.contains(s));
    }

    public boolean isEmpty() {
        Set<State> result = new HashSet<>();
        Stack<State> workingList = new Stack<>();
        workingList.push(startState);
        while (!workingList.isEmpty()) {
            State current = workingList.pop();
            result.add(current);
            getAllPossibleSuccessors(current).stream().filter(s -> !result.contains(s))
                    .forEach(workingList::push);
        }
        return result.stream().noneMatch(s -> finalStates.contains(s));
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