import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Tree {

    // Root node
    private TreeNode root;

    // This boolean is just used determining the name of the output file in "writeToFile()"
    private boolean hasBeenCompressed = false;

    // General Constructor: Start the tree with just a root node
    Tree() {
        root = new TreeNode(null, null);
    }

    /**
     * Adds a node to the Tree containing the specified data, with the specified node as its parent
     * @param data The data to be stored in the new node
     * @param parent The node to be set as the parent of the new ndoe
     * @return The newly added node
     */
    public TreeNode addNode(String data, TreeNode parent) {
        // If "null" is passed in as the parent, the root node should be set
        // as the parent. This makes creating the tree in QuerySidekick easier.
        if (parent == null) parent = root;
        // Create the new TreeNode()
        TreeNode newNode = new TreeNode(data, parent);
        // This line simultaneously adds the newNode to "parent"'s list of
        // children, and returns the child node (prevents nodes with duplicate data)
        return parent.addChild(newNode);
    }

    /**
     * Recursively merges a node with it's children if a given node meets the following criteria:
     * 1. The node is never the end of a search phrase (frequency = 0)
     * 2. The node only has one child
     * @param startNode
     */
    public void compress(TreeNode startNode) {
        // If "null" is passed in for "startNode", set the start node to be the root node
        if (startNode == null) startNode = root;

        // Attempt to compress children (if the current node has any)
        for (TreeNode t : startNode.getChildren()) {  compress(t);  }

        // If the current node has a frequency of 0 and only
        // has one child, merge the current node with its child
        if (startNode.getFrequency() == 0 && startNode.getChildren().length == 1)
        startNode.mergeChild();
    
        // Mark the tree as having been compressed
        hasBeenCompressed = true;
    }

    // Getter method
    public TreeNode getRoot() { return root; }

    // "toString()"" simply calls "generateString()", since it is easier to generate
    // the tree String using recursion but easier to call "toString()" by convention
    public String toString() {  return generateString(root, new ArrayList<Boolean>()); }

    /**
	 * Returns the data stored within this class as a formatted String
     * @param startingNode The node this call of "generatedString() should start at"
     * @param includeLines A boolean array that keeps track of how lines should be drawn
	 * @return The data stored within this class as a formatted String
     */
    private String generateString(TreeNode startingNode, ArrayList<Boolean> includeLines) {
		// Create a StringBuilder to store the String as it is created
        StringBuilder sb = new StringBuilder();
        // To start, there should be a vertical line between children of
        // this node. Add "true" to "includeLines"
        includeLines.add(true);
        // For each child of this "startingNode"...
        for (int i = 0; i < startingNode.getChildren().length; i++) {
            // Get the current child
            TreeNode t = startingNode.getChildren()[i];
            // Print the series of vertical lines as desired...
            for (int j = 0; j < includeLines.size(); j++) {
                if (includeLines.get(j)) {
                    if (j == includeLines.size() - 1) sb.append("|----");
                    else sb.append("|    ");
                } else sb.append("     ");
            }
            // If the last child is the one currently being printed, vertical lines
            // should not be printed under it. Set the last value in "includeLines" to false
            if (i == startingNode.getChildren().length - 1) includeLines.set(includeLines.size() - 1, false);
            // Add a String in the following format to the StringBuilder: "[data] ([passing frequency] / [frequency])"
            sb.append(String.format("%s (%d / %d)\n",t.toString(), t.getPassingFrequency(), t.getFrequency()));
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
        if (hasBeenCompressed) filename += "CompressedTree.txt";
        else filename += "Tree.txt";

        // Attempt to use a FileWriter to write the String from "toString()" to a text file
        FileWriter fw;
        try {
            fw = new FileWriter(filename);
            if (hasBeenCompressed) {
                fw.write("*** This text file contains a compressed version of the tree data stored in\n"
                        +"*** \"Tree.txt\" text file. Text at the same level of indentation represents\n"
                        +"*** nodes on the same layer of the tree. Vertical lines branch off onto horizontal\n"
                        +"*** lines which connect parent nodes to child nodes. Each node contains a phrase or a\n"
                        +"*** part of a phrase, as well as a passing frequency and a ending frequency. The passing\n"
                        +"*** frequency (first number) is the number of times the string stored in the node appears\n"
                        +"*** in any search phrase. The ending frequency (second number) is the number of times the\n"
                        +"*** string stored in the node ends a search phrase. Happy reading!\n\n");
            } else {
                fw.write("*** This text file contains a tree representation of the data stored in\n"
                        +"*** the old query text file. Text at the same level of indentation represents\n"
                        +"*** nodes on the same layer of the tree. Vertical lines branch off onto horizontal\n"
                        +"*** lines which connect parent nodes to child nodes. Each node contains a phrase or a\n"
                        +"*** part of a phrase, as well as a passing frequency and a ending frequency. The passing\n"
                        +"*** frequency (first number) is the number of times the string stored in the node appears\n"
                        +"*** in any search phrase. The ending frequency (second number) is the number of times the\n"
                        +"*** string stored in the node ends a search phrase. Happy reading!\n\n");
            }
            fw.write(toString());
            fw.close();
        } catch (IOException e) {
            // If there are any issues writing to the output file, print an error message
            System.out.println("ERROR:\t Unable to save tree representation of old queries to \"" + filename + "\"");
        }
    }

}
