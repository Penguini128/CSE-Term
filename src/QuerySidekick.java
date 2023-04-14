import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*

  Authors (group members):
  Email addresses of group members:
  Group name:

  Course:
  Section:

  Description of the overall algorithm:


*/


public class QuerySidekick
{
    
    
    String[] guesses = new String[5];  // 5 guesses from QuerySidekick
    Tree searchTree = new Tree(); // Stores data tree
    GuessTree guessTree = new GuessTree();
    long barTime; // Used for periodic printing of progress bar
    long startTime;
    int guessCount = 0; // Not used for much yet, could be useful

    int misses = 0;

    // initialization of ...
    public QuerySidekick()
    {

    }

    // process old queries from oldQueryFile
    //
    // to remove extra spaces with one space
    // str2 = str1.replaceAll("\\s+", " ");
    public void processOldQueries(String oldQueryFile)
    {

        // Attempt to create a Scanner to read the old query file
        File file = new File(oldQueryFile);
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            // If the file is not found, print an error message
            System.out.println("ERROR:\t Old query file not found");
            return;
        }

        // Tracks the current line number being read from the input file
        int currentLine = 0;

        // While there are more lines to read...
        while (scanner.hasNextLine()) {
            // Increment "currentLine"
            currentLine++;
            // Call garbage collector manually every 100 lines
            if (currentLine % 100 == 0) System.gc();

            // Get the input line and split it into space-separated tokens
            String line = scanner.nextLine();
            line = line.replaceAll("\\s+", " ");
            String[] tokens = line.split(" ");

            // Keep track of the last added node from this search phrase
            TreeNode lastNode = null;
            // For each word in the search phrase
            for (int i = 0; i < tokens.length; i++) {
                // Add the word from the search phrase to the tree, using
                // the lase added node from this phrase as the parent
                String s = tokens[i];
                lastNode = searchTree.addNode(s, lastNode);
                // Always increment passing frequency. Increment frequency
                // if the current node is the last node in the search phrase
                lastNode.incrementPassingFrequency();
                if (i == tokens.length - 1) lastNode.incrementFrequency();
            }
        }

        // Compress tree
        searchTree.compress(null);

        SearchPhraseList.addPhrases(searchTree.getRoot());
        SearchPhraseList.calculateWeights();

        guessTree.build(SearchPhraseList.getPhraseArray());

        scanner.close();

    }

    // based on a character typed in by the user, return 5 query guesses in an array
    // currChar: current character typed in by the user
    // currCharPosition: position of the current character in the query, starts from 0
    public String[] guess(char currChar, int currCharPosition)
    {
        // All this does right now is call an occasional garbage collect to prevent spike in memory usage
        guessCount++;
        if (guessCount % 1000 == 0) System.gc();
        if (currCharPosition == 0) guessTree.reset();
        return guessTree.getGuess(currChar);
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
    public void feedback(boolean isCorrectGuess, String correctQuery)        
    {
        if (!isCorrectGuess && correctQuery != null) {
            misses++;
            //System.out.println("SEARCH PHRASE MISSED: " + correctQuery + ", " + misses);
        }
    }
}
