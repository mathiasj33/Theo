import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public final class CYK {
    private CYK() {
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in); //new FileInputStream(new File("test/E1"))
        Grammar g = Grammar.parse(scanner);
        String word;
        while (!(word = scanner.nextLine()).equals("DONE")) {
            System.out.println(word + "\n" + cyk(g, word) + "\n");
        }
        scanner.close();
    }

    public static CYKTable cyk(Grammar cnf, String word) {
        CYKChart chart = new CYKChart(cnf, word);
        chart.fillChart();
        return new CYKTable(chart);
    }
}
