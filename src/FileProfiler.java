import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class FileProfiler {

	// Whether or not to output debug text files after processing old queries
	private static boolean OUTPUT_DEBUG_TEXT_FILES = true;
	private static boolean COMPARE_FILE_CONTENTS = true;

	// How often (in file lines) the progress bar should be printed
    private static final int BAR_DISPLAY_INTERVAL = 6000;
    // How long (in characters) the progress bar should be
    private static final int BAR_LENGTH = 30;

	private static long barTime = 0;
	private static long startTime;
	private static final Tree searchTree = new Tree(); // Stores data tree
    private static final GuessTree guessTree = new GuessTree();
	public static void main(String args[]) {
		final String oldQueryFile = args[0];
		final String newQueryFile = args[1];

		

		// Attempt to create a Scanner to read the old query file
        File file = new File(oldQueryFile);

		String directoryName = oldQueryFile.substring(0, oldQueryFile.length()-4) + "Profile";
		File directory = new File(directoryName);
		directory.mkdir();

        Scanner scanner;
        int lines;
        try {
            scanner = new Scanner(file);
            // Find how many lines are in the old query file by
            // going through he whole file, then reset the scanner
            lines = countAndBurnLines(scanner);
            scanner.close();
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            // If the file is not found, print an error message
            System.out.println("ERROR:\t Old query file not found");
            return;
        }

        // Initialize the start time (used for periodically printing progress bar)
        System.out.println();
        startTime = System.currentTimeMillis();
        barTime = startTime;

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
			attemptPrintBar(currentLine, lines);

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
        searchTree.writeToFile(directoryName + "\\" + oldQueryFile);

        if (COMPARE_FILE_CONTENTS) {
            try {
                scanner = new Scanner(file);
                ArrayList<String> fileContents = new ArrayList<String>();
                while (scanner.hasNextLine()) {
                    String[] tokens = scanner.nextLine().split(" ");
                    for (String s : tokens) {
                        if (!fileContents.contains(s)) fileContents.add(s);
                    }
                }
                scanner.close();
                scanner = new Scanner(new File(newQueryFile));
                int count = 1;
                while (scanner.hasNextLine()) {
                    String[] tokens = scanner.nextLine().split(" ");
                    for (String s : tokens) {
                        if (!fileContents.contains(s)) {
                            //System.out.printf("New word #%d detected: %s\n", count, s);
                            count++;
                            fileContents.add(s);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            scanner.close();
		}

		
        // Compress tree
        searchTree.compress(null);

        SearchPhraseList.addPhrases(searchTree.getRoot());
        SearchPhraseList.calculateWeights();

        // If enabled, output tree text file after
        // compression, as well as dictionary text file
        if (OUTPUT_DEBUG_TEXT_FILES) {
            searchTree.writeToFile(directoryName + "\\" + oldQueryFile);
            Dictionary.writeToFile(directoryName + "\\" + oldQueryFile);
            SearchPhraseList.writeToFile(directoryName + "\\" + oldQueryFile);
        }

		System.out.println();

		System.out.println("Generating guess tree... This may take a while...\n");
        guessTree.build(SearchPhraseList.getPhraseArray());

        if (OUTPUT_DEBUG_TEXT_FILES)
        guessTree.writeToFile(directoryName + "\\" + oldQueryFile);

		System.out.println("Profiling done!\n");
        scanner.close();
	}

	private static void attemptPrintBar(int currentLine, int lines) {

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
}
