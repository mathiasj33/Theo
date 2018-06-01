import java.util.Objects;

public class UnorderedPair<A, B> {
    public A a;
    public B b;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnorderedPair<?, ?> pair = (UnorderedPair<?, ?>) o;
        return Objects.equals(a, pair.a) &&
                Objects.equals(b, pair.b) ||
                Objects.equals(a, pair.b) &&
                        Objects.equals(b, pair.a);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a) + Objects.hash(b);
    }

    public UnorderedPair(A a, B b) {
        this.a = a;
        this.b = b;
    }
}
