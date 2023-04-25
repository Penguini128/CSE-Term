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
 * Dictionary: This class handles the storage and fetching of all words in the old
 * 			   query file. This class contains methods to add new words and update
 * 			   the frequencies of preexisting words, as well as methods to fetch words
 * 			   based on index, frequency, or a starting substring. The main idea with
 * 			   this Dictionary is to only store unique words, and store them in a single
 * 			   string that can be indexed with a lookup table. Then all other areas of the
 * 			   program that need to store a String from the old query file can instead just
 * 			   store the necessary index to access the word in the Dictionary via the
 * 			   lookup table. This allows for huge saves in memory usage.
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Dictionary {

	// This String stores all unique words found in the old query file
	// (This String is printed at the end of the dictionary output text file)
	private static String dictionary = "";

	// This ArrayList contains the indexes of the start of each word in the "dictionary" String
	private static ArrayList<Integer> lookupTable = new ArrayList<Integer>();
	// This ArrayList contains the frequency of the word at the corresponding index in "lookupTable"
	private static ArrayList<Integer> lookupFrequencies = new ArrayList<Integer>();
	// Stores the five most frequent words in the old query file
	private static String[] topFiveWords = new String[5];
	// Stores the substring to compare words to when generating new guesses
	private static StringBuilder likeyAgainst = new StringBuilder();
	// Stores some likely words in the search based on "likelyAgainst"
	private static String[] likelyFiveWords = new String[5];

	// Used for storing word length and word index in a single string
	private static final int MODULO_DATA_STOREAGE = 1000000;
	// Used for getString() formatting
	private static final short DICTIONARY_BLOCK_SIZE = 100;

	/**
	 * Adds a new word to the Dictionary, if the word is unique. Words
	 * are added lexicographically, with no duplicate words being added.
	 * @param s The String to be added to the dictionary
	 */
	public static void add(String s) {
		// See if the word is already in the dictionary.
		// If it is, do not add. Update it's frequency and return
		short foundIndex = find(s);
		if (foundIndex != -1) {
			lookupFrequencies.set(foundIndex, lookupFrequencies.get(foundIndex) + 1);
			return;
		}

		// If the word is not already in the dictionary, perform
		// a binary search to find where the new word should be inserted
		short low = 0;
		short high = (short)(lookupTable.size() - 1);
		while (low <= high) {
			short mid = (short)((low + high) / 2);
			if (get(mid).compareTo(s) < 0) low = (short)(mid + 1);
			else high = (short)(mid - 1);
		}
		// At this point, "low" will be equal to the index where the new word
		// should be inserted. If the word should be inserted at the end of the list...
		if (low == lookupTable.size()) {
			// Add the the word to the end of the list
			lookupTable.add(dictionary.length() + MODULO_DATA_STOREAGE*s.length());
			lookupFrequencies.add(1);
		} else {
			// Otherwise, insert the word at the appropriate index in the list
			lookupTable.add(low, dictionary.length() + MODULO_DATA_STOREAGE*s.length());
			lookupFrequencies.add(low, 1);
		}

		// Add the word to the "dicitonary" String
		dictionary += s;
	}

	/**
	 * Find and store possible words that could be guessed that start
	 * with substring "s"
	 * @param s The substring that the found words should start with
	 */
	public static void findLikelyFive(String s) {
		// If "s" is equal to the last String used to generate
		// the list of possible words, those guesses are
		// already saved. Return
		if (s.equals(likeyAgainst.toString())) return;
		// If "s" is the start of a new word entirely
		if (s.indexOf(likeyAgainst.toString()) != 0) {
			// Replace the String in "likelyAgainst" with "s"
			likeyAgainst.setLength(0);
			likeyAgainst.append(s);
		// Otherwise, add the new character at the end
		// of "s" to "likelyAgainst"
		} else likeyAgainst.append(s.charAt(s.length() - 1));
		
		// Use binary search to find the first word that
		// starts with the substring stored in "likelyAgainst"
		short low = 0;
		short high = (short)(lookupTable.size() - 1);
		while (low <= high) {
			short mid = (short)((low + high) / 2);
			if (get(mid).compareTo(s) < 0) low = (short)(mid + 1);
			else high = (short)(mid - 1);
		}
		
		// By this point, "low" is equal to the index of the first
		// word which starts with the substring stored in
		// "likelyAgainst". For up to five words...
		for (int i = 0; i < 5; i++) {
			// If the end of the lookup table was reached, break
			if (low + i == lookupTable.size()) break;
			// Add the word to the list of likely words
			likelyFiveWords[i] = get(low + i);
		}
		
	}
	
	// Getter methods
	public static String[] getTopFive() { return topFiveWords; }
	public static String getLikely(int index) { return likelyFiveWords[index];}
	

	/**
	 * Finds and returns the index of the specified word in the lookup table
	 * @param s The string whose index should be found
	 * @return The index where "s" can be found in the lookup table
	 */
	public static short find(String s) {
		// Find the location of the desired word via binary search
		short low = 0;
		short high = (short)(lookupTable.size() - 1);
		while (low <= high) {
			short mid = (short)((low + high) / 2);
			if (get(mid).equals(s)) {
				// If the word is found, return its index in the lookup table
				return mid;
			} else if (get(mid).compareTo(s) < 0) low = (short)(mid + 1);
			else high = (short)(mid - 1);
		}
		// If the word is not found, return the flag value -1
		return (short)-1;
	}

	/**
	 * Returns the word stored at the specified index within the dictionary
	 * @param index The index of the word to be retrieved
	 * @return The word stored at the specified index
	 */
	public static String get(int index) {
		// Note: This method assumes the passed in index is within the bounds 
		// of "lookupTable". Under normal use, this should not be an issue

		int stringIndex = lookupTable.get(index) % MODULO_DATA_STOREAGE;
		int stringLength = lookupTable.get(index) / MODULO_DATA_STOREAGE;

		// If the index is not the last index in the lookup table, return the substring of "dictionary"
		// between the starting index of the desired word and the starting index of the next word
		return dictionary.substring(stringIndex, stringIndex + stringLength);
	}

	/**
	 * Finds the five highest frequency words and stores
	 * the to an array to be accessed later via "getTopFive()"
	 */
	public static void findHighFrequencies() {

		// Create an array to temporarily hold values
		int[] tempTop = new int[5];
		// By default the array is populated with -1 at each index
		for (int i = 0; i < 5; i++) { tempTop[i] = -1; }

		// For each element in the lookup table...
		for (int i = 0; i < lookupTable.size(); i++) {
			// For each index in the top five array...
			for (int j = 0; j < 5; j++) {
				// If the current index is not populated or if the current indexes frequency
				// is higher than the frequency of the element that was previously there...
				if (tempTop[j] == -1 || lookupFrequencies.get(i) > lookupFrequencies.get(tempTop[j])) {
					// Swap in the new high frequency element into the array
					int hold = tempTop[j];
					tempTop[j] = i;
					// For all lower ranked elements...
					for (int k = j + 1; k < 5; k++) {
						// Shift the element up one index to make
						// room for the new insertion
						int temp = tempTop[k];
						tempTop[k] = hold;
						hold = temp;
					}
					break;
				}
			}
			
		}
		// Stores the top five values into the more permanent "topFiveWords" array
		for (int i = 0; i < 5; i++) {
			topFiveWords[i] = get((short)tempTop[i]);
		}
	}


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
			sb.append(String.format("%-6d | %11d | %-6d | %s\n", i, lookupTable.get(i), lookupFrequencies.get(i), get(i)));
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