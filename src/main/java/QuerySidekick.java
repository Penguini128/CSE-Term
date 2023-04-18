/*

  Authors (group members):
  Email addresses of group members:
  Group name:

  Course:
  Section:

  Description of the overall algorithm:


*/


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class QuerySidekick {

    private final Trie trie = new Trie();

    private TrieNode prevNode = trie.getRoot();

    private String query = "";

    private final Set<String> guessed = new HashSet<>();

    private final String[] guesses = new String[5];  // 5 guesses from QuerySidekick

    // initialization of ...
    public QuerySidekick() {
    }

    // process old queries from oldQueryFile
    //
    // to remove extra spaces with one space
    // str2 = str1.replaceAll("\\s+", " ");
    public void processOldQueries(String oldQueryFile) throws IOException {
        FileReader fr = new FileReader(oldQueryFile);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();

        while (line != null) {
            line = line.replaceAll("\\s+", " ");
            trie.add(line);
            line = br.readLine();
        }

        trie.print();
    }

    // based on a character typed in by the user, return 5 query guesses in an array
    // currChar: current character typed in by the user
    // currCharPosition: position of the current character in the query, starts from 0
    public String[] guess(char currChar, int currCharPosition) {
        // TODO
//        if (!prevNode.children.containsKey(currChar)) {
//            return guesses;
//        }
//
//        prevNode = prevNode.children.get(currChar);
//	    query += currChar;
//
//        List<Entry> candidates = trie.getStringsWithPrefix(prevNode, query);
//
//        // TODO: preprocessing and dp when adding
//        PriorityQueue<Entry> pq = new PriorityQueue<>(
//                (o1, o2) -> Integer.compare(o2.weight.endFrequency, o1.weight.endFrequency));
//
//        pq.addAll(candidates);
//
//        for (int i = 0; i < 5; i++) {
//            if (pq.isEmpty()) {
//                break;
//            }
//
//            Entry candidate = pq.poll();
//
//            if (guessed.contains(candidate.s)) {
//                i--;
//                continue;
//            }
//
//            guesses[i] = candidate.s;
//            guessed.add(guesses[i]);
//        }
//
//        // TODO: find anything close words using LCS when more guesses left
//        // first shown query, catching typo

        return guesses;
    }

    // feedback on the 5 guesses from the user
    // isCorrectGuess: true if one of the guesses is correct
    // correctQuery: 3 cases:
    // a.  correct query if one of the guesses is correct
    // b.  null if none of the guesses is correct, before the user has typed in 
    //            the last character
    // c.  correct query if none of the guesses is correct, and the user has 
    //            typed in the last character
    // That is:
    // Case       isCorrectGuess      correctQuery   
    // a.         true                correct query
    // b.         false               null
    // c.         false               correct query
    public void feedback(boolean isCorrectGuess, String correctQuery) {
        // DEBUG
//        if (isCorrectGuess) {
//            System.out.println("Correct! " + query);
//        } else if (correctQuery == null) {
//            System.out.println("Failed! " + query);
//        } else {
//            System.out.println("Correct at the end! " + query);
//        }

        if (correctQuery != null) {
            prevNode = trie.getRoot();
            query = "";
            guessed.clear();

            trie.add(correctQuery);
        }
    }

}
