import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptPDATest {

    @Test
    public void accept() {
        PDA pda = TestUtils.loadPDA("test_res/acceptPDA");
        assertEquals(true, AcceptPDA.accept(pda, "ab"));
    }
}