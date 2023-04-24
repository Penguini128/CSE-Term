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
    int phraseCount = 0;
    StringBuilder currentKnownWords = new StringBuilder();
    StringBuilder currentIncomingWord = new StringBuilder();


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
        Scanner wordScanner;
        Scanner phraseScanner;
        try {
            wordScanner = new Scanner(file);
            phraseScanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            // If the file is not found, print an error message
            System.out.println("ERROR:\t Old query file not found");
            return;
        }

        // Tracks the current line number being read from the input file
        int currentLine = 0;

        // While there are more lines to read...
        while (wordScanner.hasNextLine()) {
            // Increment "currentLine"
            currentLine++;
            // Call garbage collector manually every 10 lines
            if (currentLine % 10 == 0) System.gc();

            // Get the input line and split it into space-separated tokens
            String line = wordScanner.nextLine();
            line = line.replaceAll("\\s+", " ");
            String[] tokens = line.split(" ");

            for (int i = 0; i < tokens.length; i++) {
                Dictionary.add(tokens[i]);
            }
        }

        while (phraseScanner.hasNextLine()) {
            // Increment "currentLine"
            currentLine++;
            // Call garbage collector manually every 10 lines
            if (currentLine % 10 == 0) System.gc();
            // Get the input line and split it into space-separated tokens
            String line = phraseScanner.nextLine();
            line = line.replaceAll("\\s+", " ");
            String[] tokens = line.split(" ");
            // Create an array that will be used to store the indexes required to
            // fetch the phrase from the Dictionary
            short[] lookupIndices = new short[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                lookupIndices[i] = Dictionary.find(tokens[i]);
            }
            // Add the new phrase to the PhraseList (or increment the
            // frequency of a preexisting phrase)
            PhraseList.addPhrase(new PhraseNode(lookupIndices));
        }

        Dictionary.findHighFrequencies();

        // Generate the guess tree
        guessTree.build();

        wordScanner.close();
        phraseScanner.close();

    }

    // based on a character typed in by the user, return 5 query guesses in an array
    // currChar: current character typed in by the user
    // currCharPosition: position of the current character in the query, starts from 0
    public String[] guess(char currChar, int currCharPosition)
    {
        // Garbage collect occasionally to prevent memory spike
        guessCount++;
        if (guessCount % 300 == 0) System.gc();

        // If a new phrase is being guessed, reset the guess tree
        if (currCharPosition == 0) {
            currentKnownWords.setLength(0);
            currentIncomingWord.setLength(0);
            guessTree.reset();
            phraseCount++;
        }

        if (currChar == ' ') {
            currentKnownWords.append(currentIncomingWord.toString());
            currentKnownWords.append(' ');
            currentIncomingWord.setLength(0);
        } else currentIncomingWord.append(currChar);

        String guesses[] = guessTree.getGuess(currChar);
        if (guesses[4] != null) return guesses;
        // Fetch guesses from the guess tree and return them
        for (int i = 4; i >= 0; i--) {
            if (guesses[i] != null) break;
            if (currChar == ' ') {
                guesses[i] = currentKnownWords.toString() + Dictionary.getTopFive()[4 - i];
            } else {
                Dictionary.findLikelyFive(currentIncomingWord.toString());
                guesses[i] = currentKnownWords.toString() + Dictionary.getLikely(4 - i);
            }

        }
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
    public void feedback(boolean isCorrectGuess, String correctQuery) { }
}
