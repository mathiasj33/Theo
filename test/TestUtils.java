import tmp2.Grammar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.fail;

public class TestUtils {
    public static Grammar loadGrammar(String path) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(new File(path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("Couldn't find grammar");
        }
        return Grammar.parse(scanner);
    }
}
