import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

public class AcceptNFA {

    public static boolean accept(EpsilonNFA n, String w) {
        char[] chars = w.toCharArray();
        if (notInAlphabet(n, chars)) return false;
        Set<State> current = n.epsilonClosure(n.getStartState());
        for (char c : chars) {
            current = n.computeReachableStates(current, c);
        }
        for (State s : current) {
            if (n.getFinalStates().contains(s)) return true;
        }
        return false;
    }

    private static boolean notInAlphabet(EpsilonNFA n, char[] chars) {
        for(char c : chars) {
            if(!n.getAlphabet().contains(c)) return true;
        }
        return false;
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EpsilonNFA e = Parser.parse(scanner);
        String word = scanner.nextLine();
        while (!word.equals("DONE")) {
            System.out.println(word + ": " + accept(e, word));
            word = scanner.nextLine();
        }
        scanner.close();
    }
}
