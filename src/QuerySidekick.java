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
    
    Tree searchTree = new Tree();
    String[] guesses = new String[5];  // 5 guesses from QuerySidekick
    long startTime;
    int guessCount = 0;
    int lines = 0;

    private final boolean DISPLAY_PROGRESS_BAR = true;
    private final int BAR_DISPLAY_INTERVAL = 800;
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

        File file = new File(oldQueryFile);
        Scanner scanner;
        try {
            scanner = new Scanner(file);
            burnLines(scanner);
            scanner.close();
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR:\t Old query file not found");
            return;
        }

        startTime = System.currentTimeMillis();

        int ticker = 0;
        while (scanner.hasNextLine()) {
            ticker++;
            if (ticker % 100 == 0) System.gc();

            if (DISPLAY_PROGRESS_BAR) attemptPrintBar(ticker, lines);

            String line = scanner.nextLine();
            line = line.replaceAll("\\s+", " ");
            String[] tokens = line.split(" ");
            TreeNode lastNode = null;
            for (int i = 0; i < tokens.length; i++) {
                String s = tokens[i];
                lastNode = searchTree.addNode(s, lastNode);
                lastNode.incrementPassingFrequency();
                if (i == tokens.length - 1) lastNode.incrementFrequency();
            }
        }

        searchTree.writeToFile(oldQueryFile);

        searchTree.compress(null);

        searchTree.writeToFile(oldQueryFile);

        Dictionary.writeToFile(oldQueryFile);

        scanner.close();

    }

    // based on a character typed in by the user, return 5 query guesses in an array
    // currChar: current character typed in by the user
    // currCharPosition: position of the current character in the query, starts from 0
    public String[] guess(char currChar, int currCharPosition)
    {
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

    private void attemptPrintBar(int ticker, int lines) {

        if (System.currentTimeMillis() - startTime < BAR_DISPLAY_INTERVAL) return;

        startTime += BAR_DISPLAY_INTERVAL;
        StringBuilder sb = new StringBuilder();
        float fillPercent = ticker / (float)lines;
        int fillInt = (int)(BAR_LENGTH * fillPercent);
        for (int i = 0; i < fillInt; i++) { sb.append("\u25A0");  }
        for (int i = fillInt; i < BAR_LENGTH; i++) { sb.append(" "); }
        System.out.println(String.format("Progress: %4.1f%% [%s] %d/%d",
                fillPercent * 100, sb.toString(), ticker, lines));
        
    }

    private void burnLines(Scanner scanner) {
        while (scanner.hasNextLine()) {
            scanner.nextLine();
            lines++;
            if (lines % 1000 == 0) System.gc();
        }
    }

}
