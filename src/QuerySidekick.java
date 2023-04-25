/*
 * Authors (group members): Tommy Galletta
 *                          Dongwook Kim
 *                          Xander Lockard
 *                          Ioana Silaghi
 *
 * Email addresses of group members: tgalletta2022@my.fit.edu
 *                                   kimd2019@my.fit.edu
 *                                   alockard2022@my.fit.edu
 *                                   isilaghi2023@my.fit.edu
 * Group name: TuringIncomplete

 * Course: CSE 2010
 * Section: 1/4
 *
 * Description of the overall algorithm: Scan through the old query file twice, first to store all
 *                                       unique words in the file and their frequencies, and second
 *                                       to store all unique phrases in the file and their frequencies.
 *                                       Then generate a tree which stores all of the most likely guesses
 *                                       for a given sequence of letters in the old query file. Use this
 *                                       tree to generate guesses for new searches as they are received.
 *                                       In the event that no guesses in the tree correspond to the
 *                                       incoming search, use rudimentary guessing methods to guess
 *                                       words that match incoming phrase.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class QuerySidekick {

    String[] guesses = new String[5];  // 5 guesses from QuerySidekick

    // Stores all unique search phrases in old query file lexicograpically and by frequency
    GuessTree guessTree = new GuessTree();
    // Used to occasionally perform garbage collection
    int guessCount = 0;
    // Keeps tracks of the current complete and incoming words being "typed"
    StringBuilder currentKnownWords = new StringBuilder();
    StringBuilder currentIncomingWord = new StringBuilder();


    // initialization of ...
    public QuerySidekick() { }

    // Process old queries from oldQueryFile
    public void processOldQueries(String oldQueryFile) {
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

        // The following while loop populates the Dictionary with
        // all of the unique words in the old query input file
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

            // Add each of the words from the input line into the Dictionary
            for (int i = 0; i < tokens.length; i++) {
                Dictionary.add(tokens[i]);
            }
        }

        // The following while loop populates the PhraseList with
        // all of the unique phrases in the old query input file
        // While there are more lines to read...
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

        // Find and store the five highest frequency words in the Dicitonary
        Dictionary.findHighFrequencies();

        // Generate the guess tree
        guessTree.build();

        wordScanner.close();
        phraseScanner.close();

    }

    // Based on a character typed in by the user, return 5 query guesses in an array
    // currChar: current character typed in by the user
    // currCharPosition: position of the current character in the query, starts from 0
    public String[] guess(char currChar, int currCharPosition)
    {
        // Garbage collect occasionally to prevent memory spike
        guessCount++;
        if (guessCount % 300 == 0) System.gc();

        // If a new phrase is being guessed, reset the guess tree as
        // well as clearing "currentKnownWords" and "currentIncomingWord"
        if (currCharPosition == 0) {
            currentKnownWords.setLength(0);
            currentIncomingWord.setLength(0);
            guessTree.reset();
        }

        // If the current word being typed is a space
        if (currChar == ' ') {
            // Add the newly completed word to "currentKnownWords"
            // and clear "currentIncomingWord"
            currentKnownWords.append(currentIncomingWord.toString());
            currentKnownWords.append(' ');
            currentIncomingWord.setLength(0);
        // Otherwise, add the new character to the current incoming word
        } else currentIncomingWord.append(currChar);

        // Get the 5 most likely guesses from the guess tree
        String guesses[] = guessTree.getGuess(currChar);

        // If 5 guesses were retrieved from the guess tree, return the guesses
        if (guesses[4] != null) return guesses;

        // If the method reaches this point, the guess tree has partially
        // or completely run out of guesses for this search phrase
        // For each unpopulated index in in the guess array...
        for (int i = 4; i >= 0; i--) {
            // Break if the current index is actually populated
            if (guesses[i] != null) break;
            // If the current character is a space...
            if (currChar == ' ') {
                // Guess all known words, followed by the most frequent words
                guesses[i] = currentKnownWords.toString() + Dictionary.getTopFive()[4 - i];
            } else {
                // Otherwise, find words in the dictionary that start with the
                // substring of the current incoming word and guess all known
                // words, followed by those words
                Dictionary.findLikelyFive(currentIncomingWord.toString());
                guesses[i] = currentKnownWords.toString() + Dictionary.getLikely(4 - i);
            }
        }
        // Return the list of found guesses
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

    // Our program does not make use of the feedback method
    public void feedback(boolean isCorrectGuess, String correctQuery) { }
}
