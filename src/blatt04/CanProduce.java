import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CanProduce {

    public static boolean canProduce(Grammar g, String w){
        return g.containsWord(w);
    }

    

    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in); //new FileInputStream(new File("test/sampleB.txt"))
        Grammar g = Grammar.parse(scanner);
        String word = scanner.nextLine();
        while(!word.equals("DONE")) {
            System.out.println(word + ": " + canProduce(g,word));
            word = scanner.nextLine();
        }
        scanner.close();
    }
}
