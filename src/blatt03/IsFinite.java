import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class IsFinite {

    public static boolean isFinite(DFA d) {
        return d.isFinite();
    }


    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in); //TODO: System.in
        EpsilonNFA e = Parser.parse(scanner);
        DFA d = null;
        if (e instanceof DFA) {
            d = (DFA) e;
        } else {
            System.out.println("No DFA provided, aborting");
            System.exit(3);
        }
        System.out.println(isFinite(d));
        scanner.close();
    }

}

