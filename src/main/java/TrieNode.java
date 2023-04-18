import java.util.LinkedList;
import java.util.List;

public class TrieNode {

    /**
     * a-z(0-25), 0-9(26-35), space(36), _(37)
     */
    public final int SYMBOLS = 38;

    private final TrieNode[] children = new TrieNode[SYMBOLS];

    private final int table;
    private final List<Integer> candidates = new LinkedList<>();
    private int start;
    private int end;
    private boolean isQuery;

    public TrieNode(int table, int start, int end, boolean isQuery) {
        this.table = table;
        this.start = start;
        this.end = end;
        this.isQuery = isQuery;
    }

    private int getSymbolIndex(char symbol) {
        if (symbol >= 'a' && symbol <= 'z') {
            return symbol - 'a'; // 0-25
        } else if (symbol >= '0' && symbol <= '9') {
            return 26 + symbol - '0'; // 26-35
        } else if (symbol == ' ') {
            return 36; // 36
        } else if (symbol == '_') {
            return 37; // 37
        }
        return -1; // error
    }

    /**
     * getters and setters
     */

    public TrieNode getChild(int index) {
        return children[index];
    }

    public TrieNode getChild(char symbol) {
        return getChild(getSymbolIndex(symbol));
    }

    public void setChild(char symbol, TrieNode child) {
        children[getSymbolIndex(symbol)] = child;
    }

    public int getTable() {
        return table;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean isQuery() {
        return isQuery;
    }

    public void setQuery(boolean query) {
        isQuery = query;
    }

    public List<Integer> getCandidates() {
        return candidates;
    }

}
