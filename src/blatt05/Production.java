import java.util.*;

public class Production implements Comparable<Production> {
    public List<NonTerminal> left;
    public List<Atom> right;

    Production(String l, String r) {
        left = new ArrayList<>();
        for (char c : l.toCharArray()) {
            left.add(new NonTerminal("" + c));
        }
        right = new ArrayList<>();
        for (char c : r.toCharArray()) {
            Atom a = Character.isUpperCase(c) ? new NonTerminal("" + c) : new Terminal(c);
            right.add(a);
        }
    }

    Production(NonTerminal left, Atom right) {
        this(Collections.singletonList(left), Collections.singletonList(right));
    }

    Production(NonTerminal left, Atom... right) {
        this(Collections.singletonList(left), Arrays.asList(right));
    }

    Production(List<NonTerminal> left, Atom right) {
        this(left, Collections.singletonList(right));
    }

    Production(NonTerminal left, List<Atom> right) {
        this(Collections.singletonList(left), right);
    }

    Production(List<NonTerminal> left, List<? extends Atom> right) {
        if (left.isEmpty()) {
            throw new IllegalArgumentException("Empty production");
        }
        this.left = left.size() == 1
                ? Collections.singletonList(left.get(0))
                : Collections.unmodifiableList(new ArrayList<>(left));
        if (right.isEmpty()) {
            this.right = Collections.singletonList(new Terminal());
        } else if (right.size() == 1) {
            this.right = Collections.singletonList(right.get(0));
        } else {
            this.right = Collections.unmodifiableList(new ArrayList<>(right));
        }
    }

    public Set<Production> getAllEpsilonCombinations(Set<Atom> epsilonNTs) {
        Set<Production> newProductions = new HashSet<>();
        List<Integer> ntPositions = getNTPositions(epsilonNTs);
        if(ntPositions.isEmpty()) return new HashSet<>();
        List<Set<Integer>> powerNTs = powerSet(ntPositions);
        for (Set<Integer> s : powerNTs) {
            newProductions.add(generateNewProduction(s));
        }
        return newProductions;
    }

    private List<Integer> getNTPositions(Set<Atom> epsilonNTs) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < right.size(); i++) {
            if(epsilonNTs.contains(right.get(i))) positions.add(i);
        }
        return positions;
    }

    public Production generateNewProduction(Set<Integer> set) {
        List<Atom> newRight = new ArrayList<>();
        for(int i = 0; i < right.size(); i++) {
            if(!set.contains(i)) newRight.add(right.get(i));
        }
        return new Production(left, newRight);
    }

    public static <E> List<Set<E>> powerSet(List<E> original) {
        List<Set<E>> newList = new ArrayList<>();
        if(original.size() == 1) {
            Set<E> trivialSet = new HashSet<>();
            trivialSet.add(original.get(0));
            newList.add(trivialSet);
            newList.add(new HashSet<>());
            return newList;
        }
        E first = original.remove(0);
        List<Set<E>> recursive = powerSet(original);
        for (Set<E> s : recursive) {
            newList.add(s);
            Set<E> newSet = new HashSet<>();
            newSet.addAll(s);
            newSet.add(first);
            newList.add(newSet);
        }
        return newList;
    }

    @Override
    public String toString() {
        //do not print the greek letter epsilon for empty right side
        return String.join(" ", Util.toString(left)) + " -> "
                + (right.isEmpty() ? "" : String.join(" ", Util.toString(right)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Production)) {
            return false;
        }
        Production that = (Production) o;
        return Objects.equals(left, that.left) && Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    public NonTerminal onlyLeft() {
        if (left.size() != 1) {
            throw new IllegalStateException("Expected only one left production, got " + left);
        }
        return left.get(0);
    }

    public Atom onlyRight() {
        if (right.size() != 1) {
            throw new IllegalStateException("Expected only one right production, got " + right);
        }
        return right.get(0);
    }

    public boolean isEpsilon() {
        return right.size() == 1 && right.get(0) instanceof Terminal &&
                ((Terminal) right.get(0)).isEpsilon();
    }

    @Override
    public int compareTo(Production o) {
        int compare = compareLexicographic(left.iterator(), o.left.iterator());
        return compare != 0 ? compare : compareLexicographic(right.iterator(), o.right.iterator());
    }

    private static <T extends Comparable<? super T>> int
    compareLexicographic(Iterator<? extends T> left, Iterator<? extends T> right) {
        while (left.hasNext() && right.hasNext()) {
            int compare = left.next().compareTo(right.next());
            if (compare != 0) {
                return compare;
            }
        }
        if (left.hasNext()) {
            return 1;
        }
        if (right.hasNext()) {
            return -1;
        }
        return 0;
    }
}
