import java.util.*;

public final class AcceptPDA {
    private AcceptPDA() {
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PDA m = PDAParser.parse(scanner);
        String word = scanner.nextLine();
        while (!word.equals("DONE")) {
            System.out.println(word + ": " + accept(m, word));
            word = scanner.nextLine();
        }
        scanner.close();
    }

    public static boolean accept(PDA m, String w) {
        for (char c : w.toCharArray()) {
            if (!m.getAlphabet().contains(c)) return false;
        }

        int iterations = 35;

        Map<State, List<PDATransition>> state2transitions = new HashMap<>();
        for (PDATransition t : m.getTransitions()) {
            state2transitions.putIfAbsent(t.start, new ArrayList<>());
            state2transitions.get(t.start).add(t);
        }

        Set<Configuration> configurations = new HashSet<>();
        List<Configuration> toAdd = new ArrayList<>();
        configurations.add(new Configuration(m.getStartState(), w, "" + m.getStartSymbol()));

        for (int i = 0; i < iterations; i++) {
            toAdd.clear();
            if (configurations.isEmpty()) return false;
            for (Configuration c : configurations) {
                char nextChar = c.word.length() > 0 ? c.word.charAt(0) : Util.EPSILON;
                char tos = c.stack.length() > 0 ? c.stack.charAt(0) : Util.EPSILON;
                for (PDATransition t : state2transitions.get(c.state)) {
                    if (t.popSymbol != tos) continue;
                    if (t.label == nextChar || t.label == Util.EPSILON) {
                        Configuration newConf = new Configuration(t.end, t.label == Util.EPSILON ? c.word : c.word.substring(1),
                                t.pushSymbols + c.stack.substring(1));
                        if(newConf.equals(c)) continue;
                        if(newConf.word.equals("") && newConf.stack.equals("")) return true;
                        toAdd.add(newConf);
                    }
                }
            }
            configurations.clear();
            configurations.addAll(toAdd);
        }

        return false;
    }

    public static Set<PDATransition> getAvailableTrans(PDA m, State s, char sym) {
        Set<PDATransition> result = new HashSet<>();
        for (PDATransition t : m.getTransitions()) {
            if (t.getStart().equals(s) && t.getPopSymbol().equals(sym)) {
                result.add(t);
            }
        }
        return result;
    }
}
