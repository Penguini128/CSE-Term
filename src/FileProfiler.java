import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class FileProfiler {

	// Whether or not to output debug text files after processing old queries
	private static boolean OUTPUT_DEBUG_TEXT_FILES = true;

	// How often (in file lines) the progress bar should be printed
    private static final int BAR_DISPLAY_INTERVAL = 6000;
    // How long (in characters) the progress bar should be
    private static final int BAR_LENGTH = 30;

	private static long startTime;
	private static final Tree searchTree = new Tree(); // Stores data tree
    private static final GuessTree guessTree = new GuessTree();
    private static int guessCount = 0;
    private static int misses = 0;
    private static int oldMisses = 0;
    private static int oldLines = 0;
    private static int newLines = 0;
    private static int newWords = 0;
    private static int unrecognizedPhrases = 0;
    private static int unrecognizedWords = 0;
    private static int uniqueUnrecognizedPhrases = 0;
    private static int uniqueUnrecognizedWords = 0;

    private static final ArrayList<String> newPhraseList = new ArrayList<String>();
    private static final ArrayList<String> newWordList = new ArrayList<String>();
    private static final ArrayList<ProfileNode> oldQueriesMissed = new ArrayList<ProfileNode>();

	public static void main(String args[]) {

        if (args.length != 2) {
		    System.err.println("Usage: FileProfiler oldQueryFile newQueryFile");
		    return;
	    }

		final String oldQueryFile = args[0];
		final String newQueryFile = args[1];

		// Attempt to create a Scanner to read the old query file
        File file = new File(oldQueryFile);

        Scanner scanner;
        try {
            scanner = new Scanner(file);
            // Find how many lines are in the old query file by
            // going through he whole file, then reset the scanner
            oldLines = countAndBurnLines(scanner);
            scanner.close();
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            // If the file is not found, print an error message
            System.out.println("ERROR:\t Old query file not found");
            return;
        }

        String directoryName = oldQueryFile.substring(0, oldQueryFile.length()-4) + "Profile";
		File directory = new File(directoryName);
		directory.mkdir();

        // Initialize the start time (used for periodically printing progress bar)
        System.out.println();
        startTime = System.currentTimeMillis();

        // Tracks the current line number being read from the input file
        int currentLine = 0;

		System.out.println("Generating old query frequency tree...\n");

        // While there are more lines to read...
        while (scanner.hasNextLine()) {
            // Increment "currentLine"
            currentLine++;
            // Call garbage collector manually every 100 lines
            if (currentLine % 100 == 0) System.gc();

            // If enabled, check if the progress bar should be printed
			attemptPrintBar(currentLine, oldLines);

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
        if (OUTPUT_DEBUG_TEXT_FILES)
        searchTree.writeToFile(directoryName + "\\");
		
        // Compress tree
        searchTree.compress(null);

        PhraseList.addPhrases(searchTree.getRoot());
        PhraseList.calculateWeights();

        // If enabled, output tree text file after
        // compression, as well as dictionary text file
        if (OUTPUT_DEBUG_TEXT_FILES) {
            searchTree.writeToFile(directoryName + "\\");
            Dictionary.writeToFile(directoryName + "\\");
            PhraseList.writeToFile(directoryName + "\\");
        }

		System.out.println();

		System.out.println("Generating guess tree... This may take a while...\n");
        guessTree.build(PhraseList.getPhraseArray());

        if (OUTPUT_DEBUG_TEXT_FILES)
        guessTree.writeToFile(directoryName + "\\");

        System.out.println("Analyzing new queries... This may take a while...\n");
        file = new File(newQueryFile);
        Scanner input;
        try {
            input = new Scanner(file);
            newLines = countAndBurnLines(input);
            input.close();
            input = new Scanner(file);
            newWords = countAndBurnWords(input);
            input.close();
            input = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR:\tUnable to fild new query file");
            scanner.close();
            return;
        }

        while(input.hasNextLine()) {
            String query = input.nextLine(); 
            if (!PhraseList.contains(query)) {
                unrecognizedPhrases++;
                if (!newPhraseList.contains(query)) {
                    uniqueUnrecognizedPhrases++;
                }
            }
            if (!newPhraseList.contains(query)) newPhraseList.add(query);
            String[] tokens = query.split(" ");
            for (String s : tokens) {

                if (Dictionary.contains(s) == -1) {
                    unrecognizedWords++;
                    if (!newWordList.contains(s)) {
                        uniqueUnrecognizedWords++;
                    }
                }
                if (!newWordList.contains(s)) newWordList.add(s);
            }
           //remove extra spaces with one space
           query = query.replaceAll("\\s+", " ");
           //Remove punctuation from each query
           //query = query.replaceAll("[^a-zA-Z]", "");
   
           //Stores the number of characters in the query.
           int noOfCharactersInQuery = query.length();
           int indexCharacter = 0;
           boolean isCorrectGuess = false;
   
           //Go through every character in the query, and stop if a correct guess was made.
           while(indexCharacter < noOfCharactersInQuery && !isCorrectGuess){
           //Record start time of the guess
           //Each character is passed to the QuerySidekick program to return 3 gussess
           String[] guesses = guess(query.charAt(indexCharacter), indexCharacter);
           //To calculate the time taken for each guess operation
                       
           //Go through the guesses, to see whether there was a correct guess
           String correctGuess = null;
           for(int indexGuess=0; indexGuess < 5; indexGuess++){
               //If there was a correct guess, call the feedback method and calculate percentage of characters skipped
               if(query.equalsIgnoreCase(guesses[indexGuess])){
               isCorrectGuess = true;
               correctGuess = guesses[indexGuess];
               //Calculates the percentage of characters skipped
               break;
               }
           }
           //This is to call feedback
           //If the character entered was the last character in the query, then pass the correct query to the feedback
           if(indexCharacter == noOfCharactersInQuery - 1)
               feedback(isCorrectGuess, query);
           else
               feedback(isCorrectGuess, correctGuess);
   
           //Increment counter to check next character in the query
           indexCharacter++;
           }
        }

        createContentAnalysisFile(directoryName + "\\");

		System.out.println("Profiling done!\n");
        input.close();
        scanner.close();
	}

	private static void attemptPrintBar(int currentLine, int lines) {

        // If not enough time has passed since last print, return
        if (currentLine % BAR_DISPLAY_INTERVAL != 1 && currentLine != lines) return;
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

    private static int countAndBurnLines(Scanner scanner) {
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

    private static int countAndBurnWords(Scanner scanner) {
        // Scan through whole file, counting the number of lines
        int words = 0;
        while (scanner.hasNextLine()) {
            words += scanner.nextLine().split(" ").length;
        }
        // Return the number of lines in the input file
        return words;
    }

    public static String[] guess(char currChar, int currCharPosition) {
        guessCount++;
        if (guessCount % 1000 == 0) System.gc();
        if (currCharPosition == 0) guessTree.reset();
        return guessTree.getGuess(currChar);
    }

    public static void feedback(boolean isCorrectGuess, String correctQuery) {
        if (!isCorrectGuess && correctQuery != null) {
            misses++;
             if (PhraseList.contains(correctQuery)) {
                oldMisses++;
                for (ProfileNode f : oldQueriesMissed) {
                    if (f.data.equals(correctQuery)) {
                        f.frequency++;
                        return;
                    }
                }
                oldQueriesMissed.add(new ProfileNode(correctQuery));
             }
        }
    }

    public static void createContentAnalysisFile(String filename) {
        filename += "ContentAnalysis.txt";

        // Attempt to use a FileWriter to write the String from "toString()" to a text file
        FileWriter fw;
        try {
            fw = new FileWriter(filename);
            fw.write(String.format("Number of search phrases in old query file: %d\n"
                                 + "Number of unique phrases in old query file: %d\n"
                                 + "Number of unique words in old query file: %d\n"
                                 + "\n"
                                 + "Number of search phrases in new query file: %d\n"
                                 + "Number of unique phrases in new query file: %d\n"
                                 + "Number of unique words in new query file: %d\n"
                                 + "\n"
                                 + "Number of unrecognized search phrases in new query file: %d\n"
                                 + "Number of unrecognized words in new query file: %d\n"
                                 + "\n"
                                 + "Number of unique unrecognized search phrases in new query file: %d\n"
                                 + "Number of unique unrecognized words in new query file: %d\n"
                                 + "\n"
                                 + "Percentage of recognized search phrases in new query file: %.2f%%\n"
                                 + "Percentage of unrecognized search phrases in new query file: %.2f%%\n"
                                 + "\n"
                                 + "Percentage of recognized words in new query file: %.2f%%\n"
                                 + "Percentage of unrecognized words in new query file: %.2f%%\n"
                                 + "\n"
                                 + "Number of missed guesses: %d\n"
                                 + "Number of missed guesses that existed in old queries: %d\n"
                                 + "Percentage of new queries missed: %.2f%%\n"
                                 + "Percentage of new queries that were missed despite being old queries: %.2f%%\n"
                                 + "\n"
                                 , oldLines, PhraseList.size(), Dictionary.size()
                                 , newLines, newPhraseList.size(), newWordList.size()
                                 , unrecognizedPhrases, unrecognizedWords
                                 , uniqueUnrecognizedPhrases, uniqueUnrecognizedWords
                                 , (newLines - unrecognizedPhrases)/(float)newLines * 100f, unrecognizedPhrases / (float)newLines *100f
                                 , (newWords - uniqueUnrecognizedWords)/(float)newWords * 100f, unrecognizedWords / (float)newWords* 100f
                                 , misses, oldMisses, misses / (float)newLines * 100f, oldMisses / (float)newLines * 100f));
            fw.write("Missed old queries (along with # of times missed):\n");
            for (ProfileNode p : oldQueriesMissed) {
                fw.write(String.format("%-4d | %s\n", p.frequency, p.data));
            }
            fw.close();
        } catch (IOException e) {
            // If there are any issues writing to the output file, print an error message
            System.out.println("ERROR:\t Unable to save tree representation of old queries to \"" + filename + "\"");
        }
    }
}
