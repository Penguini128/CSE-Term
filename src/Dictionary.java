import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Dictionary {
	private static String dictionary = "";
	private static ArrayList<Integer> lookupTable = new ArrayList<Integer>();

	public static int add(String s) {
		int foundIndex = contains(s);
		if (foundIndex != -1) return foundIndex;

		lookupTable.add(dictionary.length());
		dictionary += s;
		return lookupTable.size() - 1;

	}

	public static String get(int index) {
		if (index < lookupTable.size() - 1) 
		return dictionary.substring(lookupTable.get(index), lookupTable.get(index + 1));

		return dictionary.substring(lookupTable.get(index));
	}

	private static int contains(String s) {
		for (int i = 0; i < lookupTable.size() - 1; i++) {
			if (dictionary.substring(lookupTable.get(i), lookupTable.get(i + 1)).equals(s)) {
				return i;
			}
		}
		if (lookupTable.size() > 0 && 
			dictionary.substring(lookupTable.get(lookupTable.size() - 1)).equals(s)) 
		return lookupTable.size() - 1;

		return -1;
	}

	public static String getString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lookupTable.size(); i++) {
			sb.append(String.format("%-6d | %s\n",
									i, get(i)));
		}
		return sb.toString();
	}

	public static void writeToFile(String filename) {
        String dictionaryString = getString();

        String treeFileName = filename.substring(0, filename.length() - 4) + "Dictionary.txt";
        FileWriter fw;
        try {
            fw = new FileWriter(treeFileName);
            fw.write(dictionaryString);
            fw.close();
        } catch (IOException e) {
            System.out.println("ERROR:\t Unable to save tree representation of old queries to \"" + treeFileName + "\"");
        }
    }
}