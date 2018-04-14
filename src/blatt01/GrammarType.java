package blatt01;

import java.util.Scanner;

public class GrammarType {


    public static boolean isType0(Grammar g) {
        return true;
    }

    public static boolean isType1(Grammar g) {
        for (Production p : g.getProductions()) {
            if(isStartToEta(g, p)) continue;
            if (p.getLeftSide().length() > p.getRightSide().length()) return false;
        }
        return true;
    }

    public static boolean isType2(Grammar g) {
        if (!isType1(g)) return false;
        for (Production p : g.getProductions()) {
            if(isStartToEta(g, p)) continue;
            String ls = p.getLeftSide();
            if(ls.length() > 1) return false;
            if(!g.getNonTerminals().contains(ls.charAt(0))) return false;
        }
        return true;
    }

    public static boolean isType3(Grammar g) {
        if(!isType2(g)) return false;
        for (Production p : g.getProductions()) {
            if(isStartToEta(g, p)) continue;
            String rs = p.getRightSide();
            if(!g.getAlphabet().contains(rs.charAt(0))) return false;
            if(rs.length() > 1 && !g.getNonTerminals().contains(rs.charAt(1))) return false;
            if(rs.length() > 2) return false;
        }
        return true;
    }

    private static boolean isStartToEta(Grammar g, Production p) {
        String ls = p.getLeftSide();
        String rs = p.getRightSide();
        return ls.length() == 1 && g.getStartingSymbol() == ls.charAt(0) && rs.equals("");
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        Grammar g = Grammar.parseGrammar(s);
        System.out.println(isType0(g));
        System.out.println(isType1(g));
        System.out.println(isType2(g));
        System.out.println(isType3(g));
    }

}
