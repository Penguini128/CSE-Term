import java.util.ArrayList;
public class GuessNode {

    char guessCharacter;
    GuessNode parent;
    int[] guesses;
    ArrayList<GuessNode> children;

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
        int currentSearchIndex = 0;
        for (char c : Dictionary.alphabet) {
            GuessNode newChild = new GuessNode(c, this);
            String childPhrase = newChild.getPhraseSoFar();
            while (SearchPhraseList.getPhrase(currentSearchIndex).indexOf(childPhrase) == 0) {
                newChild.rank(currentSearchIndex);
                currentSearchIndex++;
                if (currentSearchIndex == phraseList.size()) break;
            }
            if (newChild.guesses[0] == -1) children.remove(newChild);
        }

        for (int i : guesses) {
            if (i != -1) SearchPhraseList.setUsed(i);
        }

        for (GuessNode child : children) {
            System.out.println(child.getGuessString());
            //child.build(phraseIndex + 1, phraseList);
        }
    }

    public ArrayList<GuessNode> getChildren() { return children; }

    public String getPhraseSoFar() {
        String output = String.valueOf(guessCharacter);
        GuessNode currentNode = parent;
        while (currentNode.parent != null) {
            output = String.valueOf(currentNode.guessCharacter) + output;
        }
        return output;
    }

    public String getGuessString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (int i = 0; i < guesses.length; i++) {
            if (guesses[i] == -1) sb.append("null");
            else sb.append(SearchPhraseList.getPhrase(guesses[i]));
            if (i < guesses.length - 1) sb.append(", ");
        }
        sb.append(" ]");
        return sb.toString();
    }

    private void rank(int phraseIndex) {
        if (SearchPhraseList.isUsed(phraseIndex)) return;
        int[] newGuesses = new int[5];
        int phraseFrequency = SearchPhraseList.getPhraseFrequency(phraseIndex);
        if (phraseFrequency <= SearchPhraseList.getPhraseFrequency(guesses[4])) return;
        int offset = 0;
        // For each child in the previous child array...
        for (int i = 0; i < guesses.length - 1; i++) {
            // If the new child has not been inserted but should be inserted at this index...
            if (offset == 0 && phraseFrequency > SearchPhraseList.getPhraseFrequency(guesses[i])) {
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
        System.out.println(getGuessString());
    }
    
}
