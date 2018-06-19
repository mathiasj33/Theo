package tmp;

public final class Palindrome {
    private Palindrome() {
    }

    public static void main(String[] args) {
        printPalindromePDA();
    }


    //q;a;S;q;SS
    public static void printPalindromePDA() {
        System.out.println("Case #1:\n"
                + "PDA\n"
                + "Alphabet: 0;1\n"
                + "States: p;q\n"
                + "Init: p\n"
                + "Stackalphabet: 0;1;Z\n"
                + "Startsymbol: Z\n"
                + "Transitions:\n"
                + "p;0;Z;p;0Z\n"
                + "p;1;Z;p;1Z\n"
                + "p;0;0;p;00\n"
                + "p;1;0;p;10\n"
                + "p;0;1;p;01\n"
                + "p;1;1;p;11\n"
                + "p;;Z;q;\n"
                + "p;1;Z;q;\n"
                + "p;0;Z;q;\n"
                + "p;;0;q;0\n"
                + "p;;1;q;1\n"
                + "p;0;0;q;0\n"
                + "p;1;0;q;0\n"
                + "p;0;1;q;1\n"
                + "p;1;1;q;1\n"
                + "q;0;0;q;\n"
                + "q;1;1;q;\n"
                + "q;;Z;q;\n"
                + "END");
    }
}
