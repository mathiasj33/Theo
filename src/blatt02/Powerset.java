import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Powerset {

    private static Map<Set<State>, State> stateConverter = new HashMap<>();

    public static DFA powersetConstruction(NFA n) {
        Set<Set<State>> states = new HashSet<>();

        Set<Transition> transitions = new HashSet<>();
        List<Set<State>> workingSet = new ArrayList<>();

        Set<State> startSet = new HashSet<>();
        startSet.add(n.getStartState());
        states.add(startSet);
        workingSet.add(startSet);

        while (!workingSet.isEmpty()) {
            Set<State> set = workingSet.remove(0);
            for (char c : n.getAlphabet()) {
                Set<State> reach = n.computeReachableStates(set, c);
                if (!states.contains(reach)) {
                    workingSet.add(reach);
                    states.add(reach);
                }
                transitions.add(new Transition(getState(set), getState(reach), c));
            }
        }

        Set<State> convertedStates = states.stream().map(Powerset::getState).collect(Collectors.toSet());
        Set<Set<State>> finalStates = states.stream().filter(set ->
                set.stream().anyMatch(s -> n.getFinalStates().contains(s))
        ).collect(Collectors.toSet());
        Set<State> convertedFinalStates = finalStates.stream().map(Powerset::getState).collect(Collectors.toSet());
        return new DFA(convertedStates, transitions, n.getAlphabet(), getState(startSet), convertedFinalStates);
    }

    private static State getState(Set<State> set) {
        if (stateConverter.containsKey(set)) return stateConverter.get(set);
        State newState = new State();
        stateConverter.put(set, newState);
        return newState;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in); //TODO: System.in. new FileInputStream(new File("test.txt"))
        scanner.nextLine();//remove the number of lines in the beginning
        EpsilonNFA e = Parser.parse(scanner);
        scanner.close();
        NFA n = null;
        if (e instanceof NFA) {
            n = (NFA) e;
        } else {
            System.out.println("No NFA provided, aborting");
            System.exit(3);
        }
        System.out.println("Case #1:\n" + powersetConstruction(n));
    }


}
