import java.util.Scanner;

public class Equivalent {

    public static boolean equivalent(DFA d1, DFA d2) {
        return complementProduct(d1, d2).isEmpty() && complementProduct(d2, d1).isEmpty();
    }

    private static DFA complementProduct(DFA d1, DFA d2) {
        return DFA.productConstruction(d1, d2, (a, b) -> a && !b);
    }

    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);
        EpsilonNFA e = Parser.parse(scanner);
        DFA d1 = null;
        if(e instanceof DFA){
            d1 = (DFA) e;
        }
        else{
            System.out.println("First automaton is no DFA, aborting");
            System.exit(3);
        }

        e = Parser.parse(scanner);
        DFA d2 = null;
        if(e instanceof DFA){
            d2 = (DFA) e;
        }
        else{
            System.out.println("Second automaton is no DFA, aborting");
            System.exit(3);
        }
        
        System.out.println(equivalent(d1,d2));

        scanner.close();
    }
}
