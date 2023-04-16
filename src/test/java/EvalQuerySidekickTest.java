import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class EvalQuerySidekickTest {

    @Test
    public void input1() throws IOException {
        String[] args = { "src/test/resources/algorithmQueriesOld.txt", "src/test/resources/algorithmQueriesNew.txt" };
        EvalQuerySidekick.main(args);
    }

    @Test
    public void input2() throws IOException {
        String[] args = { "src/test/resources/florida_beachQueriesOld.txt", "src/test/resources/florida_beachQueriesNew.txt" };
        EvalQuerySidekick.main(args);
    }

}
