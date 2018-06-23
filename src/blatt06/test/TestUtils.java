import java.io.*;
import java.util.Scanner;

import static org.junit.Assert.fail;

public class TestUtils {
    public static TuringMachine<String> loadTM(String path) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
            return TuringMachine.parse(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("Couldn't find TM");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Parsing error");
        }
        return null;
    }

}
