import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
public class GuessTree {
    
    private GuessNode root;

    private GuessNode currentNode;

    GuessTree() {
        root = new GuessNode('-', null);
        currentNode = root;
    }

    public void build(ArrayList<TreeNode> phraseList) {
        root.build(0, phraseList);
    }

    public void reset() {
        currentNode = root;
    }

    public String[] getGuess(char guessChar) {
        
        for (GuessNode gn :currentNode.getChildren()) {
            if (gn.getGuessChar() == guessChar) {
                currentNode = gn;
                return gn.getGuesses();
            }
        }
        return currentNode.getGuesses();
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
        String treeFileName = filename.substring(0, filename.length() - 4) + "GuessTree.txt";

        // Attempt to use a FileWriter to write the String from "toString()" to a text file
        FileWriter fw;
        try {
            fw = new FileWriter(treeFileName);
            fw.write(toString());
            fw.close();
        } catch (IOException e) {
            // If there are any issues writing to the output file, print an error message
            System.out.println("ERROR:\t Unable to save tree representation of old queries to \"" + treeFileName + "\"");
        }
    }

}
