import java.util.Objects;

public class Configuration<S> {
    private S state;
    private String left;
    private String right;

    public Configuration(S state, String left, String right) {
        this.state = state;
        this.left = left;
        this.right = right;
    }


    public void executeTransition(TuringMachine.Transition<S> transition) {
        state = transition.successor;
        right = right.equals("") ? transition.letter + "" : transition.letter + right.substring(1);
        switch (transition.direction) {
            case L:
                char lastLeft = left.equals("") ? TuringMachine.EMPTY_LETTER : left.charAt(left.length() - 1);
                left = left.equals("") ? "" : left.substring(0, left.length() - 1);
                right += lastLeft;
                break;
            case R:
                left += right.charAt(0);
                right = right.substring(1);
                break;
            case N:
                break;
        }
    }

    public S getState() {
        return state;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    public char getCurrentChar() {
        return right.equals("") ? TuringMachine.EMPTY_LETTER : right.charAt(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration<?> that = (Configuration<?>) o;
        return Objects.equals(state, that.state) &&
                Objects.equals(left, that.left) &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {

        return Objects.hash(state, left, right);
    }
}