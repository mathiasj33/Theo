package tmp2;

import java.util.Scanner;

public final class CNF {
    private CNF() {
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Grammar g = Grammar.parse(scanner);
        scanner.close();

        Grammar cnf = cnf(g);
        System.out.println(cnf);
    }

    public static Grammar cnf(Grammar g) {
        g.convertToChomsky();
        return g;
    }
}
