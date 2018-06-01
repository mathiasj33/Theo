import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Minimize {


    public static void main(String[] args) throws FileNotFoundException {
    Scanner scanner = new Scanner(System.in); //new FileInputStream(new File("test_res/sample1"))
    scanner.nextLine();//remove the number of lines in the beginning
    EpsilonNFA e = Parser.parse(scanner);
    DFA d = null;
    if (e instanceof DFA) {
      d = (DFA) e;
    } else {
      System.out.println("No DFA provided, aborting");
      System.exit(3);
    }

    System.out.println("Case #1:\n" + minimize(d));
    scanner.close();
  }

    public static DFA minimize(DFA d) {
        d.minimize();
        return d;
    }

}
