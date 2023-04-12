import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SearchPhraseList {

	private static ArrayList<TreeNode> phraseArray = new ArrayList<TreeNode>();

	private static void addPhrase(TreeNode node) {
		if (node.getFrequency() != 0) phraseArray.add(node);
	}

	public static String getPhrase(int index) {
		return phraseArray.get(index).getSearchPhrase();
	}

	public static String getString() {
		StringBuilder sb = new StringBuilder();
		for (TreeNode tn : phraseArray) {
			sb.append(String.format("%-6d | %s\n", tn.getFrequency(), tn.getSearchPhrase()));
		}
		return sb.toString();
	}

	public static void addPhrases(TreeNode root) {
		addPhrase(root);
		for (TreeNode tn : root.getChildren()) {
			addPhrases(tn);
		}
	}

	public static void writeToFile(String filename) {

		// Create the file name of the output file from the original old query file name
        String treeFileName = filename.substring(0, filename.length() - 4) + "PhraseList.txt";
		// Attempt to use a FileWriter to write the String from "getString()" to a text file
        FileWriter fw;
        try {
            fw = new FileWriter(treeFileName);
            fw.write(getString());
            fw.close();
        } catch (IOException e) {
			// If there are any issues writing to the output file, print an error message
            System.out.println("ERROR:\t Unable to save tree representation of old queries to \"" + treeFileName + "\"");
        }
    }
}
