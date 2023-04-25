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
 * GuessTree: This class is responsible for maintaining a tree of guesses given a
 *            sequence of letters that has been inputted. The tree is sorted such that
 *            guesses of higher likelyhood appear in higher levels of the tree, while
 *            less likely guesses appear in lower levels. Because of the tree's
 *            structure, guesses can be obtained very quickly simply based on the
 *            last character that has been received in the input.
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GuessTree {
    
    // Root of the tree
    private GuessNode root;

    // The current node being checked in the tree
    // For each guess, this node gets updated
    private GuessNode currentNode;

    // General Constructor
    GuessTree() {
        root = new GuessNode('\n', null);
        currentNode = root;
    }

    // Building a GuessTree from the list of all phrases and their frequencies
    public void build() {
        // Build the tree by calling the recursive build method, starting in the root node
        root.build(0);
    }

    // Resets guesses back to the top of the tree
    public void reset() {
        currentNode = root;
    }

    // Retrieve and return the next list of guesses from the tree
    public String[] getGuess(char guessChar) {
        // Iterate through the children of the current guess node...
        for (GuessNode gn :currentNode.getChildren()) {
            // If the letter within the current child node corresponds with the current input letter...
            if (gn.getGuessChar() == guessChar) {
                // Update the current node to this child, and return the guesses stored within the child
                currentNode = gn;
                return gn.getGuesses();
            }
        }

        return new String[5];
    }

    // "toString()"" simply calls "generateString()", since it is easier to generate
    // the tree String using recursion but easier to call "toString()" by convention
    public String toString() {  return generateString(root, new ArrayList<Boolean>()); }

    /**
	 * Returns the data stored within this class as a formatted String
     * @param startingNode The node this call of "generatedString() should start at"
     * @param includeLines A boolean array that keeps track of how lines should be drawn
	 * @return The data stored within this class as a formatted String
     */
    private String generateString(GuessNode startingNode, ArrayList<Boolean> includeLines) {
		// Create a StringBuilder to store the String as it is created
        StringBuilder sb = new StringBuilder();
        // To start, there should be a vertical line between children of
        // this node. Add "true" to "includeLines"
        includeLines.add(true);
        // For each child of this "startingNode"...
        for (int i = 0; i < startingNode.getChildren().size(); i++) {
            // Get the current child
            GuessNode t = startingNode.getChildren().get(i);
            // Print the series of vertical lines as desired...
            for (int j = 0; j < includeLines.size(); j++) {
                if (includeLines.get(j)) {
                    if (j == includeLines.size() - 1) sb.append("|----");
                    else sb.append("|    ");
                } else sb.append("     ");
            }
            // If the last child is the one currently being printed, vertical lines
            // should not be printed under it. Set the last value in "includeLines" to false
            if (i == startingNode.getChildren().size() - 1) includeLines.set(includeLines.size() - 1, false);
            // Add a String in the following format to the StringBuilder: "[data] ([passing frequency] / [frequency])"
            sb.append(String.format("\"%s\" %s\n",t. getPhraseSoFar(), t.getGuessString()));
            // Add the String representation of the current child to the StringBuilder
            sb.append(generateString(t, includeLines));
        }
        // Before potentially returning to a higher call of this recursive method, remove the
        // last value in "includeLines" to prevent more lines being printed than necessary
        includeLines.remove(includeLines.size() - 1);

        // Return the resulting String
        return sb.toString();
    }

    /**
	* Takes the String returned by "toString()" and writes it to a text file
	* @param filename The name of the original old query file
	*/
    public void writeToFile(String filename) {

        // Create the file name of the output file from the original old query file name
        filename += "GuessTree.txt";

        // Attempt to use a FileWriter to write the String from "toString()" to a text file
        FileWriter fw;
        try {
            fw = new FileWriter(filename);
            fw.write("*** This text file contains a tree representation stored in the GuessTree\n"
                    +"*** class. Text at the same level of indentation represents\n"
                    +"*** nodes on the same layer of the tree. Vertical lines branch off onto horizontal\n"
                    +"*** lines which connect parent nodes to child nodes. Each node contains a sequence\n"
                    +"*** of letters (shownn in quotation marks) that represents the sequence of letters\n"
                    +"*** that must be received from the new query file in order to navigate the the given\n"
                    +"*** node. Following this is a series of up to 5 Strings (enclosd in sqaure brackets)\n"
                    +"*** which are the five guesses that will be made if that given sequence of letters is\n"
                    +"*** received. Happy reading!\n\n");
            fw.write(toString());
            fw.close();
        } catch (IOException e) {
            // If there are any issues writing to the output file, print an error message
            System.out.println("ERROR:\t Unable to save tree representation of old queries to \"" + filename + "\"");
        }
    }

}
