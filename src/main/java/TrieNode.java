import java.util.HashMap;
import java.util.Map;

public class TrieNode {

    public final char c;

    // TODO
    public final Map<Character, TrieNode> children = new HashMap<>();

    public Weight weight = new Weight();

    public TrieNode(char c) {
        this.c = c;
    }

}
