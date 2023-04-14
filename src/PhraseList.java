import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PhraseList {

	// An array list used to store all old query phrases
	private static ArrayList<TreeNode> phraseArray = new ArrayList<TreeNode>();

	// Gets and returns the phrase stored at a certain index of the phrase array
	public static String getPhrase(int index) {
		if (index == -1) return null;
		return phraseArray.get(index).getPhrase();
	}

	// Gets and returns the frequency of the phrase stored at a cetain index of the phrase array
	public static int getPhraseFrequency(int index) {
		if (index == -1) return 0;
		return phraseArray.get(index).getFrequency();
	}

	// Gets and returns the weight of the phrase stored at a cetain index of the phrase array
	public static float getPhraseWeight(int index) {
		if (index == -1) return 0;
		return phraseArray.get(index).getWeight();
	}

	// Returns the contents of the phrase array as a formatted String
	public static String getString() {
		StringBuilder sb = new StringBuilder();
		for (TreeNode tn : phraseArray) {
			sb.append(String.format("%-6d | %s\n", tn.getFrequency(), tn.getPhrase()));
		}
		return sb.toString();
	}

	// Adds all of the valid phrases from the old search tree to the phrase list
	public static void addPhrases(TreeNode root) {
		// If the current node holds a complete phrase, add the node
		if (root.getFrequency() != 0) phraseArray.add(root);
		// Recursively add all children of this node
		for (TreeNode tn : root.getChildren()) {
			addPhrases(tn);
		}
	}

	// Mark the node at the specified index as used (for the sake of generating the guess tree)
	public static void setUsed(int index) {
		phraseArray.get(index).setUsed();
	}

	// Returns whether or not a particular node has been used in the guess tree
	public static boolean isUsed(int index) {
		return phraseArray.get(index).isUsed();
	}

	public static int findStartIndex(String substring) {
		if (substring == null) return 0;
		for (int i = 0; i < phraseArray.size(); i++) {
			if (phraseArray.get(i).getPhrase().indexOf(substring) == 0)
			return i;
		}
		return -1;
	}

	// Updates the weights of all nodes in the phrase array
	public static void calculateWeights() {
		for (TreeNode tn : phraseArray) {
			tn.updateWeight();
		}
	}


	// Returns the whole phrase array
	public static ArrayList<TreeNode> getPhraseArray() { return phraseArray; }

	/**
	* Takes the String returned by "toString()" and writes it to a text file
	* @param filename The name of the original old query file
	*/
	public static void writeToFile(String filename) {

		// Create the file name of the output file from the original old query file name
        filename += "PhraseList.txt";
		// Attempt to use a FileWriter to write the String from "getString()" to a text file
        FileWriter fw;
        try {
            fw = new FileWriter(filename);
			fw.write("*** This text file contains all of the search phrases found in\n"
					+"*** the old query file, sorted alphabetically. Each phrase is\n"
					+"*** preceeded by a number, which represents the number of times\n"
					+"*** that phrase appears in the old query file. Happy reading!\n\n");
            fw.write(getString());
            fw.close();
        } catch (IOException e) {
			// If there are any issues writing to the output file, print an error message
            System.out.println("ERROR:\t Unable to save tree representation of old queries to \"" + filename + "\"");
        }
    }
}
