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
        EpsilonNFA<Pair<S, Boolean>> nfa = new EpsilonNFA<>(tm.getAlphabet(), new Pair<>(tm.getInitialState(), false));
        tm.getStates().forEach(s -> {
            nfa.addState(new Pair<>(s, false));
            nfa.addState(new Pair<>(s, true));
        });
        tm.getFinalStates().forEach(s -> {
            nfa.addFinalState(new Pair<>(s, false));
            nfa.addFinalState(new Pair<>(s, true));
        });

        for (Pair<S, TuringMachine.Transition<S>> pair : tm.getAllTransitions()) {
            if (pair.b.letter == TuringMachine.EMPTY_LETTER) {
                nfa.addEpsilonTransition(new Pair<>(pair.a, false), new Pair<>(pair.b.successor, true));
                nfa.addEpsilonTransition(new Pair<>(pair.a, true), new Pair<>(pair.b.successor, true));
            } else {
                nfa.addTransition(new Pair<>(pair.a, false), pair.b.letter, new Pair<>(pair.b.successor, false));
            }
        }

        //final states need to accept everything
        for (S s : tm.getFinalStates()) {
            for (char c : tm.getAlphabet()) {
                nfa.addTransition(new Pair<>(s, false), c, new Pair<>(s, false));
            }
        }

        return nfa;
    }
}
