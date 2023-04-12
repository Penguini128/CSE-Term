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

    // Whether or not to display progress bar when processing old queries
    private final boolean DISPLAY_PROGRESS_BAR = true;
    // Whether or not to output debug text files after processing old queries
    private final boolean OUTPUT_DEBUG_TEXT_FILES = true;
    // How often (in file lines) the progress bar should be printed
    private final int BAR_DISPLAY_INTERVAL = 6000;
    // How long (in characters) the progress bar should be
    private final int BAR_LENGTH = 30;

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
        int lines;
        try {
            scanner = new Scanner(file);
            // If the progress bar should be displayed...
             if (DISPLAY_PROGRESS_BAR){
                // Find how many lines are in the old query file by
                // going through he whole file, then reset the scanner
                lines = countAndBurnLines(scanner);
                scanner.close();
                scanner = new Scanner(file);
             }
        } catch (FileNotFoundException e) {
            // If the file is not found, print an error message
            System.out.println("ERROR:\t Old query file not found");
            return;
        }

        // Initialize the start time (used for periodically printing progress bar)
        if (DISPLAY_PROGRESS_BAR) {
            System.out.println();
            startTime = System.currentTimeMillis();
            barTime = startTime;
        }

        // Tracks the current line number being read from the input file
        int currentLine = 0;

        // While there are more lines to read...
        while (scanner.hasNextLine()) {
            // Increment "currentLine"
            currentLine++;
            // Call garbage collector manually every 100 lines
            if (currentLine % 100 == 0) System.gc();

            // If enabled, check if the progress bar should be printed
            if (DISPLAY_PROGRESS_BAR) attemptPrintBar(currentLine, lines);

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

        // If enabled, output tree text file before compression
        if (OUTPUT_DEBUG_TEXT_FILES) {
            System.out.println("\n/!\\: Peak memory usage may be affected by outputting debug text files");
            searchTree.writeToFile(oldQueryFile);
        }

        // Compress tree
        searchTree.compress(null);

        SearchPhraseList.addPhrases(searchTree.getRoot());

        guessTree.build(SearchPhraseList.getPhraseArray());

        // If enabled, output tree text file after
        // compression, as well as dictionary text file
        if (OUTPUT_DEBUG_TEXT_FILES) {
            searchTree.writeToFile(oldQueryFile);
            Dictionary.writeToFile(oldQueryFile);
            SearchPhraseList.writeToFile(oldQueryFile);
            guessTree.writeToFile(oldQueryFile);
        }

        // Output formatting for debug purposes
        if (DISPLAY_PROGRESS_BAR) System.out.println();

        scanner.close();

    }

    // based on a character typed in by the user, return 5 query guesses in an array
    // currChar: current character typed in by the user
    // currCharPosition: position of the current character in the query, starts from 0
    public String[] guess(char currChar, int currCharPosition)
    {
        // All this does right now is call an occasional garbage collect to prevent spike in memory usage
        guessCount++;
        if (guessCount % 10000 == 0) System.gc();
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
    public void feedback(boolean isCorrectGuess, String correctQuery)        
    {

    }

    /**
     * If sufficient time has passed, prints a progress bar during old query file processing
     * @param currentLine The current line number being processed
     * @param lines The total number of lines to process
     */
    private void attemptPrintBar(int currentLine, int lines) {

        // If not enough time has passed since last print, return
        if (currentLine % BAR_DISPLAY_INTERVAL != 1 && currentLine != lines) return;

        // Increase start time (effectively resets timer for progress bar printing)
        barTime += BAR_DISPLAY_INTERVAL;
        // Create a StringBuilder to store the String as it is created
        StringBuilder sb = new StringBuilder();
        // Calculate the completion percent, and from that the number
        // of filled spaces on the progress bar
        float completionPercent = currentLine / (float)lines;
        int fill = (int)(BAR_LENGTH * completionPercent);
        // Fill the first portion of the progress bar with filled squares
        for (int i = 0; i < fill; i++) { sb.append("\u25A0");  }
        // Fill the rest of the progress bar with spaces
        for (int i = fill; i < BAR_LENGTH; i++) { sb.append(" "); }
        // Print the progress bar, along with percent completion and fraction of lines completed
        System.out.println(String.format("Progress: %5.1f%% [%s]  Time elapsed: %.2f seconds",
                           completionPercent * 100, sb.toString(), (System.currentTimeMillis() - startTime) / 1000.0f));
        
    }

    private int countAndBurnLines(Scanner scanner) {
        // Scan through whole file, counting the number of lines
        int lines = 0;
        while (scanner.hasNextLine()) {
            scanner.nextLine();
            lines++;
            // Call an occasional garbage collect to prevent spike in memory usage
            if (lines % 1000 == 0) System.gc();
        }
        // Return the number of lines in the input file
        return lines;
    }

}
