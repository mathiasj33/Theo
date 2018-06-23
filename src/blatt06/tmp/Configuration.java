import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Configuration<S> {
    private S state;
    private List<Character> tape = new ArrayList<>();
    private int position;

    public Configuration(S state, String left, String right) {
        this.state = state;
        this.tape = new ArrayList<>();
        fillWithChars(this.tape, left + right);
        this.position = left.length();
    }

    private void fillWithChars(List<Character> list, String s) {
        for (char c : s.toCharArray()) {
            list.add(c);
        }
    }

    public void executeTransition(TuringMachine.Transition<S> transition) {
        state = transition.successor;
        if(tape.isEmpty()) tape.add(transition.letter);
        else tape.set(position, transition.letter);
        switch (transition.direction) {
            case L:
                position--;
                if(position < 0) {
                    tape.add(0, TuringMachine.EMPTY_LETTER);
                    position = 0;
                }
                break;
            case R:
                position++;
                if (position >= tape.size()) {
                    tape.add(TuringMachine.EMPTY_LETTER);
                }
                break;
            case N:
                break;
        }
    }

    public S getState() {
        return state;
    }

    public char getCurrentChar() {
        return tape.isEmpty() ? TuringMachine.EMPTY_LETTER : tape.get(position);
    }
}