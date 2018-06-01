import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UnorderedPair<T> {
    private Set<T> set = new HashSet<>();

    public UnorderedPair(T a, T b) {
        set.add(a);
        set.add(b);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnorderedPair<?> that = (UnorderedPair<?>) o;
        return Objects.equals(set, that.set);
    }

    @Override
    public int hashCode() {
        return Objects.hash(set);
    }
}
