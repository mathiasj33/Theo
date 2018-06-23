import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class ToEpsilon {
    private ToEpsilon() {
    }

    public static void main(String... args) throws IOException {
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            System.out.println(toEpsilonNFA(TuringMachine.parse(reader)));
        }
    }

    public static <S> EpsilonNFA<?> toEpsilonNFA(TuringMachine<S> tm) {
        EpsilonNFA<S> nfa = new EpsilonNFA<>(tm.getAlphabet(), tm.getInitialState());
        tm.getStates().forEach(nfa::addState);
        tm.getFinalStates().forEach(nfa::addFinalState);

        for (Pair<S, TuringMachine.Transition<S>> pair : tm.getAllTransitions()) {
            if (pair.b.letter == TuringMachine.EMPTY_LETTER) {
                //Determine if TM accepts this word after reading EMPTY_SYMBOL (a maximum of len(states) times)
                if(Simulate.simulate(tm, "", tm.getStates().size()) == Simulate.Result.ACCEPTED) {
                    nfa.addFinalState(pair.a);
                }
            } else {
                nfa.addTransition(pair.a, pair.b.letter, pair.b.successor);
            }
        }

        //final states need to accept everything
        for (S s : tm.getFinalStates()) {
            for (char c : tm.getAlphabet()) {
                nfa.addTransition(s, c, s);
            }
        }

        return nfa;
    }
}
