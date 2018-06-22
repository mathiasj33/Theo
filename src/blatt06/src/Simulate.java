import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class Simulate {
    private Simulate() {
    }

    public static void main(String... args) throws IOException {
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            TuringMachine<String> tm = TuringMachine.parse(reader);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(";");
                System.out.println(simulate(tm, split[0], Integer.parseInt(split[1])));
            }
        }
    }

    public static <S> Result simulate(TuringMachine<S> tm, String word, int bound) {
        Configuration<S> conf = new Configuration<>(tm.getInitialState(), "", word);
        for (int i = 0; i < bound; i++) {
            if(tm.isFinal(conf.getState())) return Result.ACCEPTED;
            char letter = conf.getCurrentChar();
            TuringMachine.Transition<S> t = tm.getTransition(conf.getState(), letter);
            if(t == null) return Result.STUCK;
            conf.executeTransition(t);
            if(tm.isFinal(conf.getState())) return Result.ACCEPTED;
        }
        if(tm.isFinal(conf.getState())) return Result.ACCEPTED;
        char letter = conf.getCurrentChar();
        TuringMachine.Transition<S> t = tm.getTransition(conf.getState(), letter);
        if(t == null) return Result.STUCK;
        return Result.RUNNING;
    }

    public enum Result {
        RUNNING, STUCK, ACCEPTED
    }
}
