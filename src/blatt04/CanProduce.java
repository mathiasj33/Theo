import java.util.Scanner;

public class CanProduce {

    public static boolean canProduce(Grammar g, String w){
        //TODO
        return false;
    }

    

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Grammar g = Grammar.parse(scanner);
        String word = scanner.nextLine();
        while(!word.equals("DONE")) {
            System.out.println(word + ": " + canProduce(g,word));
            word = scanner.nextLine();
        }
        scanner.close();
    }
}
