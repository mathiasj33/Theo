import java.util.*;
import java.util.stream.Collectors;

public class Production {
    final String left;
    String right;

    Production(String left, String right) {
        this.left = left;
        this.right = right;
    }

    public Set<Production> getAllEpsilonCombinations(Set<Character> epsilonNTs) {
        Set<Production> newProductions = new HashSet<>();
        List<Integer> ntPositions = getNTPositions(epsilonNTs);
        if(ntPositions.isEmpty()) return new HashSet<>();
        List<Set<Integer>> powerNTs = powerSet(ntPositions);
        for (Set<Integer> s : powerNTs) {
            newProductions.add(generateNewProduction(s));
        }
        return newProductions;
    }

    private List<Integer> getNTPositions(Set<Character> epsilonNTs) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < right.length(); i++) {
            if(epsilonNTs.contains(right.charAt(i))) positions.add(i);
        }
        return positions;
    }

    public Production generateNewProduction(Set<Integer> set) {
        String newRight = "";
        for(int i = 0; i < right.length(); i++) {
            if(!set.contains(i)) newRight += right.charAt(i);
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
        if (right.isEmpty()) {
            return left + " \u2192 " + "\u03B5"; //print the greek letter epsilon for empty right side
        } else {
            return left + " \u2192 " + right;
        }
    }

    public boolean isEpsilon() {
        return right.equals("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return Objects.equals(left, that.left) &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {

        return Objects.hash(left, right);
    }
}
