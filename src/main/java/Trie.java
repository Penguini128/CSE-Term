import java.util.LinkedList;
import java.util.List;

public class Trie {

    public TrieNode root = new TrieNode('\0');

    // TODO
    private final List<Entry> candidates = new LinkedList<>();

    public Trie() {}

    public void add(String s) {
        TrieNode cur = root;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            boolean contain = cur.children.containsKey(c);

            if (!contain) {
                cur.children.put(c, new TrieNode(c));
            }
            cur = cur.children.get(c);

            if (i != s.length() - 1) {
                cur.weight.midFrequency++;
            } else {
                cur.weight.endFrequency++;
            }
        }
    }

    public List<Entry> getStringsWithPrefix(TrieNode root, String prefix) {
        candidates.clear();
        getStrings(root, prefix);
        return candidates;
    }

    private void getStrings(TrieNode curNode, String curString) {
        if (curNode == null) {
            return;
        }

        if (curNode.weight.endFrequency > 0) {
            candidates.add(new Entry(curString, curNode.weight));
        }

        for (TrieNode child : curNode.children.values()) {
            getStrings(child, curString + child.c);
        }
    }

}
