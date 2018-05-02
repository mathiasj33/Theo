import java.util.HashSet;
import java.util.Set;


public class NFA extends EpsilonNFA {
    public NFA(Set<State> states, Set<Transition> transitions, Set<Character> alphabet, State startState, Set<State> finalStates) {
        super(states, transitions, alphabet, startState, finalStates);
        checkValidNFA();
    }

    //No epsilon transitions, rest is done by the checkValidEpsilonNFA method
    private void checkValidNFA() throws IllegalArgumentException {
        for (State s : states) {
            for (Transition t : getTransitionsFromState(s)) {
                if (t.getLabel() == Transition.EPSILON) {
                    throw new IllegalArgumentException("State " + s + " has an epsilon transition");
                }
            }
        }
    }

    public Set<State> computeReachableStates(Set<State> s, char c) {
        Set<State> result = new HashSet<>();
        for (State current : s) {
            for (Transition t : transitions) {
                if (t.getStart().equals(current) && t.getLabel() == c) {
                    result.add(t.getEnd());
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "NFA\n" + toStringHelper();
    }
}
