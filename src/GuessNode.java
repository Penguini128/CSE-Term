import java.util.ArrayList;
public class GuessNode {

    private char guessCharacter;
    private GuessNode parent;
    private int[] guesses;
    private ArrayList<GuessNode> children;

    GuessNode(char guessCharacter, GuessNode parent) {
        this.guessCharacter = guessCharacter;
        this.parent = parent;
        children = new ArrayList<GuessNode>();
        guesses = new int[5];
        for (int i = 0; i < guesses.length; i++) {
            guesses[i] = -1;
        }
    }

    public void build(int phraseIndex, ArrayList<TreeNode> phraseList) {

        
        System.gc();

        int currentSearchIndex = SearchPhraseList.findStartIndex(getPhraseSoFar());

        for (char c : Dictionary.alphabet) {
            GuessNode newChild = new GuessNode(c, this);
            String childPhrase = newChild.getPhraseSoFar();
            while (currentSearchIndex < phraseList.size()) {
                if (SearchPhraseList.getPhrase(currentSearchIndex).indexOf(childPhrase) != 0)
                break;
                newChild.rank(currentSearchIndex);
                currentSearchIndex++;
            }
            if (newChild.guesses[0] != -1) {
                for (int i : guesses) {
                    if (i != -1) SearchPhraseList.setUsed(i);
                }
                children.add(newChild);
            }
        }

        for (GuessNode child : children) {
            if (child.guesses[4] != -1)
            child.build(phraseIndex + 1, phraseList);
        }
    }

    public ArrayList<GuessNode> getChildren() { return children; }
    public char getGuessChar() { return guessCharacter; }

    public String[] getGuesses() {
        String[] stringGuesses = new String[5];
        for (int i = 0; i < guesses.length; i++) {
            stringGuesses[i] = SearchPhraseList.getPhrase(guesses[i]);
        }
        return stringGuesses;
    }

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

    public String getGuessString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        int amount = 5;
        for (int i = 0; i < guesses.length; i++) {
            if (guesses[i] == -1) {
                amount = i;
                break;
            }
        }
        for (int i = 0; i < amount; i++) {
            if (guesses[i] == -1) sb.append("null");
            else sb.append(SearchPhraseList.getPhrase(guesses[i]));
            if (i < amount - 1) sb.append(", ");
        }
        sb.append(" ]");
        return sb.toString();
    }

    private void rank(int phraseIndex) {
        if (SearchPhraseList.isUsed(phraseIndex)) return;
        int[] newGuesses = new int[5];
        float phraseFrequency = SearchPhraseList.getPhraseWeight(phraseIndex);
        if (phraseFrequency <= SearchPhraseList.getPhraseWeight(guesses[4])) return;
        int offset = 0;
        // For each child in the previous child array...
        for (int i = 0; i < guesses.length - 1; i++) {
            // If the new child has not been inserted but should be inserted at this index...
            if (offset == 0 && phraseFrequency > SearchPhraseList.getPhraseWeight(guesses[i])) {
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
