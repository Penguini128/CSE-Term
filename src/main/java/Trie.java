/**
 * Adapted from https://www.geeksforgeeks.org/compressed-tries/
 */
public class Trie {

    private final TrieTable trieTable = new TrieTable();

    private final TrieNode root = new TrieNode(-1, -1, -1, false);

    public Trie() {
    }

    // TODO
    public void add(String query) {
        TrieNode cur = root;
        int i = 0;

        // checks if the query is in the trie
        while (i < query.length() && cur.getChild(query.charAt(i)) != null) {
            TrieNode child = cur.getChild(query.charAt(i));
            String substring = trieTable.get(child.getTable(), child.getStart(), child.getEnd());
            int j = 0;

            // checks if the substring is in the query
            while (j < substring.length() && i < query.length() && substring.charAt(j) == query.charAt(i)) {
                i++;
                j++;
            }

            // if the substring is in the query, then we keep checking the next child node
            if (j == substring.length()) {
                cur = child;
                continue;
            }

            // if a prefix of the substring is in the query, and the query is shorter, then we add a new node
            if (i == query.length()) {
                TrieNode newNode = new TrieNode(trieTable.add(query), i, query.length(), true);
                child.setStart(j);
                cur.setChild(substring.charAt(0), newNode);
                newNode.setChild(substring.charAt(j), child);
                return;
            }

            // if a prefix of the substring is in the query, and the prefix is shorter, then we add two new nodes
            TrieNode newNode = new TrieNode(child.getTable(), child.getStart(), j, false);
            child.setStart(j);
            cur.setChild(substring.charAt(0), newNode);
            newNode.setChild(substring.charAt(j), child);
            newNode.setChild(query.charAt(i), new TrieNode(trieTable.add(query), i, query.length(), true));
            return;
        }

        // if the query is in the trie, then we increment its frequency
        if (i == query.length()) {
            trieTable.getWeight(cur.getTable()).incrementFrequency();
            return;
        }

        // if the query is not in the trie, then we add the query
        cur.setChild(query.charAt(i), new TrieNode(trieTable.add(query), i, query.length(), true));
    }


    // getters and setters

    public TrieNode getRoot() {
        return root;
    }

    // DEBUG REMOVE BEFORE SUBMISSION
    public void print() {
        trieTable.printTable();

        System.out.println('=' * 80);
        System.out.println("TRIE:");
        printTrie();
        System.out.println('=' * 80);
    }

    private void printTrie() {

    }

}
