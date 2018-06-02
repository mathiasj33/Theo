import java.util.Objects;

public class DijsktraNode implements Comparable<DijsktraNode> {
    public State state;
    public int cost;
    public String name;

    public DijsktraNode(State state, int cost, String name) {
        this.state = state;
        this.cost = cost;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DijsktraNode that = (DijsktraNode) o;
        return Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {

        return Objects.hash(state);
    }

    @Override
    public String toString() {
        return "DijsktraNode{" +
                "state=" + state +
                ", cost=" + cost +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int compareTo(DijsktraNode o) {
        return cost - o.cost;
    }
}
