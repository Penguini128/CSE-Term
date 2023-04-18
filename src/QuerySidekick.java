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
    GuessTree guessTree = new GuessTree();
    long barTime; // Used for periodic printing of progress bar
    int guessCount = 0; // Not used for much yet, could be useful

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
            // Call garbage collector manually every 50 lines
            if (currentLine % 50 == 0) System.gc();

            // Get the input line and split it into space-separated tokens
            String line = scanner.nextLine();
            line = line.replaceAll("\\s+", " ");
            String[] tokens = line.split(" ");

            // Create an array that will be used to store the indexes required to
            // fetch the phrase from the Dictionary
            int[] lookupIndices = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                lookupIndices[i] = Dictionary.add(tokens[i]);
            }
            // Add the new phrase to the PhraseList (or increment the
            // frequency of a preexisting phrase)
            PhraseList.addPhrase(new PhraseNode(lookupIndices));
        }

        // Generate the guess tree
        guessTree.build();

        scanner.close();

    }

    // based on a character typed in by the user, return 5 query guesses in an array
    // currChar: current character typed in by the user
    // currCharPosition: position of the current character in the query, starts from 0
    public String[] guess(char currChar, int currCharPosition)
    {
        // Garbage collect occasionally to prevent memory spike
        guessCount++;
        if (guessCount % 1000 == 0) System.gc();

        // If a new phrase is being guessed, reset the guess tree
        if (currCharPosition == 0) guessTree.reset();
        // Fetch guesses from the guess tree and return them
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

    }
}
