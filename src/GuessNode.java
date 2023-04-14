import java.util.ArrayList;
public class GuessNode {

    // GuessNode stores a parent and children like a regular tree node, but also
    // Stores a character that corresponds to letters received from a new query, as well as pre-determined guesses
    private char guessCharacter;
    private GuessNode parent;
    private int[] guesses;
    private ArrayList<GuessNode> children;

    // General Constructor
    GuessNode(char guessCharacter, GuessNode parent) {
        this.guessCharacter = guessCharacter;
        this.parent = parent;
        children = new ArrayList<GuessNode>();
        guesses = new int[5];
        // Guess index array initialized with all values as -1
        for (int i = 0; i < guesses.length; i++) {
            guesses[i] = -1;
        }
    }

    public void build(int phraseIndex, ArrayList<TreeNode> phraseList) {
        // Garbage collect to prevent memory build (this makes tree construction 
        // significantly slow, but is worth it since it's only a few seconds)
        System.gc();

        // Get the lookup index for the first phrase that matches this nodes guess phrase
        int currentSearchIndex = PhraseList.findStartIndex(getPhraseSoFar());

        // For each letter in the alphabet (this includes numbers and spaces)
        for (char c : Dictionary.alphabet) {
            // Create a new guess node
            GuessNode newChild = new GuessNode(c, this);
            // Find the sequence of letters this node will be attempting to find matching phrases for
            String childPhrase = newChild.getPhraseSoFar();
            // While there are still phrases to check...
            while (currentSearchIndex < phraseList.size()) {
                // If the current phrase does not contain the current guess phrase, break from the
                // while loop. It's time to check the next letter
                if (PhraseList.getPhrase(currentSearchIndex).indexOf(childPhrase) != 0) break;
                // If the method reaches this point, the current phrase could potentially
                // be guessed. Check to see if it should be ranked
                newChild.rank(currentSearchIndex);
                // Increment "currentSearchIndex" to check the next phrase in the next loop
                currentSearchIndex++;
            }
            // Once the code reaches this point, all valid guesses for the current guess phrase have been checked
            // If there is at least one guess...
            if (newChild.guesses[0] != -1) {
                // Set those guesses as used
                for (int i : guesses) {
                    if (i != -1) PhraseList.setUsed(i);
                }
                // Add the newly created chld node to the tree as a child of the current node
                children.add(newChild);
            }
        }
        // Once the code reached this point, all potential guesses for the next letter of the phrase have been checked
        // For each newly created child of the current node...
        for (GuessNode child : children) {
            // If it has five guesses, try to get potential follow up guesses for all next possible input letters
            if (child.guesses[4] != -1) child.build(phraseIndex + 1, phraseList);
        }
    }

    // Getter methods
    public ArrayList<GuessNode> getChildren() { return children; }
    public char getGuessChar() { return guessCharacter; }

    // Returns the guesses stored in this node as a String array
    public String[] getGuesses() {
        String[] stringGuesses = new String[5];
        for (int i = 0; i < guesses.length; i++) {
            stringGuesses[i] = PhraseList.getPhrase(guesses[i]);
        }
        return stringGuesses;
    }

    // Returns the sequence of letter that would have to be received in the
    // input in order to reach this node
    public String getPhraseSoFar() {
        if (parent == null) return null;
        String output = String.valueOf(guessCharacter);
        GuessNode currentNode = parent;
        while (currentNode.parent != null) {
            output = String.valueOf(currentNode.guessCharacter) + output;
            currentNode = currentNode.parent;
        }
        return output;
    }

    // Returns the list of guesses stored in this node as a formatted string
    public String getGuessString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (int i = 0; i < guesses.length; i++) {
            if (guesses[i] == -1) break;
            else sb.append(PhraseList.getPhrase(guesses[i]));
            if (i < guesses.length - 1 && guesses[i + 1] != -1) sb.append(", ");
        }
        sb.append(" ]");
        return sb.toString();
    }

    /**
     * Determines if a new incoming phrase should be placed within the top 5 guesses for this node
     * @param phraseIndex The index of the phrase to be ranked
     */
    private void rank(int phraseIndex) {
        // If the phrase has already been guessed by a parent node, it should not be ranked. Return
        if (PhraseList.isUsed(phraseIndex)) return;
        // Get the weight of the lowest ranked phrase in the top 5. If the new phrase is not ranked
        // higher than it, the new phrase should not be ranked. Return
        float phraseFrequency = PhraseList.getPhraseWeight(phraseIndex);
        if (phraseFrequency <= PhraseList.getPhraseWeight(guesses[4])) return;
        // Initialize an array to store the new rankings
        int[] newGuesses = new int[5];

        /*
         * The following is nearly identical to the TreeNode array insert code. It
         * just insests the new phrase in its approriate spot and gets rid of the lowest
         * ranking phrase
         */
        int offset = 0;
        // For each child in the previous child array...
        for (int i = 0; i < guesses.length - 1; i++) {
            // If the new child has not been inserted but should be inserted at this index...
            if (offset == 0 && phraseFrequency > PhraseList.getPhraseWeight(guesses[i])) {
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
