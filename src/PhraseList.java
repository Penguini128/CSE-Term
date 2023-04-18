import java.io.FileWriter;
import java.io.IOException;
\public class PhraseList {

	// An array list used to store all old query phrases
	private static int size = 0;
	private static PhraseNode head;

	// Gets and returns the phrase stored at a certain index of the phrase array
	public static String getPhrase(int index) {
		if (index == -1) return null;
		return get(index).toString();
	}
	public static PhraseNode get(int index) {
		PhraseNode current = head;
		int currentindex = 0;
		// Starting at the head we will iterate through the list.
 		// If index is invalid don't iterate.
		while (current.next != null) {
			if (currentindex == index) {
				System.out.println("test");
				return current;
			}
			else {
				current = current.next;
				currentindex++;
				// If the current node is not the right index keep moving
			}
		}
		return null;
		// Return null cause java (this should never happen)
	}

	// Gets and returns the frequency of the phrase stored at a cetain index of the phrase array
	public static int getPhraseFrequency(int index) {
		if (index == -1) return 0;
		return get(index).getFrequency();
	}

	// Returns the contents of the phrase array as a formatted String
	public static String getString() {
		StringBuilder sb = new StringBuilder();
		PhraseNode current = head;
		while (current.next != null) {
			sb.append(String.format("%-6d | %s\n", current.getFrequency(), current.toString()));
			current = current.next;
		}
		return sb.toString();
	}
	public static void addPhrase(PhraseNode node) {
		if (size == 0) {
			head = node;
			size++;
			/*
			 * If the linkedList is empty we will instantiate the head to the new node we are adding.
			 */
		}
		PhraseNode current = head; // Initialize two pointers both to head.
		PhraseNode beforeCurrent = head;
		while (current.next != null) {
			if (current.toString().equals(node.toString())) {
				current.incrementFrequency();
				return;
				// If the Nodes are identical we will just increment frequency
			}
			else {
				if (current.toString().compareTo(node.toString()) < 0) {
					// We are looking to insert the node between the two.
					if (beforeCurrent.toString().equals(head.toString())) {
						// Edge case for first iteration
						head = node;
						node.next = current;
						size++;
						return;
					}
					else {
						// Add node in between two pointers
						beforeCurrent.next = node;
						node.next = current;
						size++;
						return;
					}
				}
			}
			if (beforeCurrent.toString().equals(current.toString())) {
				// If its the first iteration don't increment beforeCurrent
				current = current.next;
			}
			else {
				// Else increment both.
				current = current.next;
				beforeCurrent = beforeCurrent.next;
			}
		}
		current.next = node;
		size++;
	}

	// Mark the node at the specified index as used (for the sake of generating the guess tree)
	public static void setUsed(int index) {
		get(index).setUsed();
	}

	// Returns whether or not a particular node has been used in the guess tree
	public static boolean isUsed(int index) {
		return get(index).isUsed();
	}

	public static int findStartIndex(String substring) {
		if (substring == null) return 0;
		PhraseNode current = head;
		int index = 0;
		while (current.next != null) {
			if (current.toString().indexOf(substring) == 0) {
				return index;
			}
			index++;
			current = current.next;
		}
		return -1;
	}


	// Returns the whole phrase array
	public static int size() { return size; }

	public static boolean contains(String s) {
		for (int i = 0; i < size(); i++) {
			if (getPhrase(i).equals(s)) return true;
		}
		return false;
	}

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
