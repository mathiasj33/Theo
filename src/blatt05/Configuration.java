import java.util.Objects;

public class Configuration {
    public State state;
    public String word;
    public String stack;

    public Configuration(State state, String word, String stack) {
        this.state = state;
        this.word = word;
        this.stack = stack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return Objects.equals(state, that.state) &&
                Objects.equals(word, that.word) &&
                Objects.equals(stack, that.stack);
    }

    @Override
    public int hashCode() {

        return Objects.hash(state, word, stack);
    }

    @Override
    public String toString() {
        return "(" +
                state +
                "," + word +
                "," + stack +
                ")";
    }
}
