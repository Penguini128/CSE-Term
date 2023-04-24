import java.util.ArrayList;
public class GuessNode {

    // GuessNode stores a parent and children like a regular tree node, but also
    // Stores a character that corresponds to letters received from a new query, as well as pre-determined guesses
    private char guessCharacter;
    private GuessNode parent;
    private short[] guesses;
    private ArrayList<GuessNode> children;

    // General Constructor
    GuessNode(char guessCharacter, GuessNode parent) {
        this.guessCharacter = guessCharacter;
        this.parent = parent;
        children = new ArrayList<GuessNode>();
        guesses = new short[5];
        // Guess index array initialized with all values as -1
        for (int i = 0; i < guesses.length; i++) {
            guesses[i] = -1;
        }
    }

    public void build(int phraseIndex) {
        // Garbage collect to prevent memory build (this makes tree construction 
        // significantly slow, but is worth it since it's only a few seconds)
        System.gc();
        String phraseSoFar = getPhraseSoFar();

        // Grab the start end end indexes to search through for the current node being built
        // This is the range of indices in the phrase array that contain phrases that start with "phraseSoFar"
        short startIndex = PhraseList.findStartIndex(phraseSoFar);
        short endIndex = PhraseList.findEndIndex(phraseSoFar, startIndex);
        // currentChar keeps track of the current character that proceeds "phraseSoFar" in the fetched phrases,
        // set to newline by default as a flag character
        char currentChar = '\n';
        GuessNode newestChild = null;

        // Iterate through all of the valid indices
        for (short i = startIndex; i < endIndex; i++) {
            char phraseChar;
            // Try to fetch the character in the current phrase proceeding "phraseSoFar". If
            // said character does not exist, the phrase is not needed, so move to the next phrase
            try {
                phraseChar = PhraseList.getPhrase(i).charAt(phraseIndex);
            } catch (IndexOutOfBoundsException e) {
                continue;
            }
            // If a new character has been found...
            if (phraseChar != currentChar) {
                // If a new child has been previously created
                if (newestChild != null) {
                    // Finalize its guesses by marking them as used
                    for (int n : newestChild.guesses) {
                        if (n != -1) PhraseList.setUsed(n);
                    }
                    // Add the child to this node's list of children
                    if (newestChild.guesses[0] != -1) children.add(newestChild);
                }
                // Update "currentChar"
                currentChar = phraseChar;
                // Create a new GuessNode that will rank guesses that
                // contain "phraseSoFar" followed by "currentChar"
                newestChild = new GuessNode(currentChar, this);
            }
            // Rank the current phrase in the newest child's guesses
            newestChild.rank(i);
        }

        // Once the method reaches this point, all valid phrases have
        // been considered. Finalize the final newest child's guesses
        if (newestChild != null) {
            for (int n : newestChild.guesses) {
                if (n != -1) PhraseList.setUsed(n);
            }
            if (newestChild.guesses[0] != -1) children.add(newestChild);
        }

        // For each child of this node (for each potential next letter following "phraseSoFar")...
        for (GuessNode child : children) {
            // If it has five guesses, try to get potential follow up guesses for all next possible input letters
            if (child.guesses[4] != -1) child.build(phraseIndex + 1);
        }
    }

    // Getter methods
    public ArrayList<GuessNode> getChildren() { return children; }
    public char getGuessChar() { return guessCharacter; }

    // Returns the guesses stored in this node as a String array
    public String[] getGuesses() {
        // Create an array to hold the guesses
        String[] stringGuesses = new String[5];
        // For each index in "guesses"
        for (int i = 0; i < guesses.length; i++) {
            // Use the value in "guesses" to fetch the corresponding phrase from the PhraseList
            stringGuesses[i] = PhraseList.getPhrase(guesses[i]);
        }
        // Return the guesses
        return stringGuesses;
    }

    // Returns the sequence of letter that would have to be received in the
    // input in order to reach this node
    public String getPhraseSoFar() {
        // If this node is the root node, it contains no phrase. Return null.
        if (parent == null) return null;
        // Otherwise, the output string starts as this nodes guess character
        String output = String.valueOf(guessCharacter);
        // Get the parent of this node
        GuessNode currentNode = parent;
        // While the current node is not the root node...
        while (currentNode.parent != null) {
            // Add the character stored in each parent to the front of "output"
            output = String.valueOf(currentNode.guessCharacter) + output;
            currentNode = currentNode.parent;
        }
        // Return the found string
        return output;
    }

    // Returns the list of guesses stored in this node as a formatted string
    public String getGuessString() {
        StringBuilder sb = new StringBuilder();
        // Start with an open brace
        sb.append("[ ");
        // For each potential guess in "guesses"...
        for (int i = 0; i < guesses.length; i++) {
            // -1 signifies no guess, therefore break from the loop
            if (guesses[i] == -1) break;
            // If this code is reached, there is a valid guess at
            // "guesses[i]". Add the correspdoning guess to the string
            else sb.append(PhraseList.getPhrase(guesses[i]));
            // If the current guess is followed by another guess, add a comma to seperate them
            if (i < guesses.length - 1 && guesses[i + 1] != -1) sb.append(", ");
        }
        // End with a closed brace
        sb.append(" ]");
        // Return the formed string
        return sb.toString();
    }

    /**
     * Determines if a new incoming phrase should be placed within the top 5 guesses for this node
     * @param phraseIndex The index of the phrase to be ranked
     */
    private void rank(short phraseIndex) {
        // If the phrase has already been guessed by a parent node, it should not be ranked. Return
        if (PhraseList.isUsed(phraseIndex)) return;
        // Get the weight of the lowest ranked phrase in the top 5. If the new phrase is not ranked
        // higher than it, the new phrase should not be ranked. Return
        float phraseFrequency = PhraseList.getPhraseFrequency(phraseIndex);
        if (phraseFrequency <= PhraseList.getPhraseFrequency(guesses[4])) return;
        // Initialize an array to store the new rankings
        short[] newGuesses = new short[5];

        /*
         * The following is nearly identical to the TreeNode array insert code. It
         * just insests the new phrase in its approriate spot and gets rid of the lowest
         * ranking phrase
         */
        int offset = 0;
        // For each child in the previous child array...
        for (int i = 0; i < guesses.length - 1; i++) {
            // If the new child has not been inserted but should be inserted at this index...
            if (offset == 0 && phraseFrequency > PhraseList.getPhraseFrequency(guesses[i])) {
                // Insert the child and update offset
                newGuesses[i] = phraseIndex;
                offset = 1;
            }
            // Insert the previous child for this iteration based on offset
            newGuesses[i + offset] = guesses[i];
        }
        // If the child was never inserted, insert the child at the end of the list
        if (offset == 0) newGuesses[4] = phraseIndex;
        guesses = newGuesses;
    }
    
}
