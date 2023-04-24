import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Dictionary {

	// This String stores all unique words found in the old query file
	// (This String is printed at the end of the dictionary output text file)
	private static String dictionary = "";

	// This ArrayList contains the indexes of the start of each word in the "dictionary" String
	private static ArrayList<Integer> lookupTable = new ArrayList<Integer>();
	private static ArrayList<Integer> lookupFrequencies = new ArrayList<Integer>();
	private static ArrayList<Short> highFrequencyWords = new ArrayList<Short>();
	private static String[] topFiveWords = new String[5];

	// This is used to format the dictionary output text file (can be mostly ignored)
	private static final short DICTIONARY_BLOCK_SIZE = 125;
	private static final int HIGH_FREQUENCY_THRESHOLD = 20;

	/**
	 * Adds a new word to the Dictionary, if the word is unique
	 * @param s The String to be added to the dictionary
	 * @return The index that can be passed into get() to retrieve the word
	 */
	public static short add(String s) {
		// See if the word is already in the dictionary.
		// If it is, return the index used to access it
		short foundIndex = contains(s);
		if (foundIndex != -1) {
			lookupFrequencies.set(foundIndex, lookupFrequencies.get(foundIndex) + 1);
			return foundIndex;
		}

		/*
		 * Add the index where the new word can be found to "lookupTable" (since
		 * the word will be added to the end of "dictionary", "dictionary.length()"
		 * will be the index where the word will start once it is added to the String
		 */
		lookupTable.add(dictionary.length());
		lookupFrequencies.add(1);
		// Add the word to the String
		dictionary += s;
		// Since the word's new index was added to the end of the end of "lookupTable",
		// return "lookupTable.size() - 1" (the last index of "lookupTable")
		return (short)(lookupTable.size() - 1);

	}

	public static String[] getTopFive() {
		return topFiveWords;
	}

	/**
	 * Returns the word stored at the specified index within the dictionary
	 * @param index The index of the word to be retrieved
	 * @return The word stored at the specified index
	 */
	public static String get(short index) {
		// Note: This method assumes the passed in index is within the bounds 
		// of "lookupTable". Under normal use, this should not be an issue

		// If the index is not the last index in the lookup table, return the substring of "dictionary"
		// between the starting index of the desired word and the starting index of the next word
		if (index < lookupTable.size() - 1) 
		return dictionary.substring(lookupTable.get(index), lookupTable.get(index + 1));

		// If the method reaches this point, it is safe to assume index == lookupTable.size() - 1,
		// return the substring from the starting index of the desired word to the end of "dictionary"
		return dictionary.substring(lookupTable.get(index));
	}

	/**
	 * Tests to see if a string exists within the Dicitionary
	 * @param s The String to be tested for its existence in "dictionary"
	 * @return The index used to access the String it is exists within
	 * 		   "dictionary", -1 if "dictionary" does not contain the String
	 */
	public static short contains(String s) {
		for (short i = 0; i < lookupTable.size(); i++) {
			if (get(i).equals(s)) return i;
		}
		// The word was not found within the Dictionary, return -1
		return -1;
	}

	public static void findHighFrequencies() {
		for (short i = 0; i < lookupFrequencies.size(); i++) {
			if (lookupFrequencies.get(i) < HIGH_FREQUENCY_THRESHOLD) continue;
			boolean inserted = false;
			for (short j = 0; j < highFrequencyWords.size(); j++) {
				if (get(i).compareTo(get((short)highFrequencyWords.get(j))) < 0) {
					highFrequencyWords.add(j, i);
					inserted = true;
					break;
				}
			}
			if (!inserted) highFrequencyWords.add(i);
		}

		int[] tempTop = new int[5];
		for (int i = 0; i < 5; i++) { tempTop[i] = -1; }

		for (int i = 0; i < lookupTable.size(); i++) {
			for (int j = 0; j < 5; j++) {
				if (tempTop[j] == -1 || lookupFrequencies.get(i) > lookupFrequencies.get(tempTop[j])) {
					int hold = tempTop[j];
					tempTop[j] = i;
					for (int k = j + 1; k < 5; k++) {
						int temp = tempTop[k];
						tempTop[k] = hold;
						hold = temp;
					}
					break;
				}
			}
			
		}
		for (int i = 0; i < 5; i++) {
			topFiveWords[i] = get((short)tempTop[i]);
		}
	}

	public static String[] getLikelyFive(String soFar) {

		String[] guesses = new String[5];

		int low = 0;
		int high = highFrequencyWords.size() - 1;
		while (low <= high) {
			int mid = (low + high) / 2;
		    if (soFar.compareTo(get(highFrequencyWords.get(mid))) > 0) low = mid + 1;
			else high = mid - 1;
		}

		for (int i = 0; i < 5; i++) {
			if (low + i < highFrequencyWords.size())
			guesses[i] = get(highFrequencyWords.get(low + i));
		}

		return guesses;
	}

	public static int size() { return lookupTable.size(); }

	/**
	 * Returns the data stored within this class as a formatted String
	 * (Note: Since this class is static I could not call it "toString()")
	 * @return The data stored within this class as a formatted String
	 */
	public static String getString() {
		// Create a StringBuilder to store the String as it is created
		StringBuilder sb = new StringBuilder();
		// For each word in the dictionary...
		for (short i = 0; i < lookupTable.size(); i++) {
			// Add a String in the following format to
			// the StringBuilder: "[index] | [word]""
			sb.append(String.format("%-6d | %s\n", i, get(i)));
		}

		// Add a newline character to separate the list
		// of unique words from the "dicitonary block"
		sb.append("\n");

		// For each letter within "dictionary"...
		for (int i = 0; i < dictionary.length(); i++) {
			// Add the letter to the StringBuilder
			sb.append(dictionary.charAt(i));
			// If the line contains as many characters
			// as "DICTIONARY_BLOCK_SIZE", add a newline character
			if (i % DICTIONARY_BLOCK_SIZE == DICTIONARY_BLOCK_SIZE - 1) sb.append("\n");
		}

		// Return the resulting String
		return sb.toString();
	}

	/**
	 * Takes the String returned by "getString()" and writes it to a text file
	 * @param filename The name of the original old query file
	 */
	public static void writeToFile(String filename) {

		// Create the file name of the output file from the original old query file name
        filename += "Dictionary.txt";
		// Attempt to use a FileWriter to write the String from "getString()" to a text file
        FileWriter fw;
        try {
            fw = new FileWriter(filename);
			fw.write("*** This text file contains every unique word found within the old query\n"
					+"*** file. Each word is preceeded by a number, which represents the index\n"
					+"*** that when passed into the Dictionary.get() method, returns the\n"
					+"*** corresponding String. For fun, at the bottom of this file, there is a\n"
					+"*** giant block of text which shows how all of the words are stored as a\n"
					+"*** singular string within the Dictionary class. Happy reading!\n\n");
            fw.write(getString());
            fw.close();
        } catch (IOException e) {
			// If there are any issues writing to the output file, print an error message
            System.out.println("ERROR:\t Unable to save tree representation of old queries to \"" + filename + "\"");
        }
    }
}