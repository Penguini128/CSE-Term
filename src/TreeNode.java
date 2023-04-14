public class TreeNode {

    // Stores the indexs used to get the corresponding words from the Dictionary
    private int[] data;
    // Frequency of stored data ending a search phrase
    private int frequency;
    // Frequency of stored data within a search phrase
    private int passingFrequency;
    // The node storing the data that preceeds the data in this node
    private TreeNode parent;
    // The nodes containing data that may proceed the data in this node
    private TreeNode[] children;

    private float weight = 0f;

    private boolean used = false;

    // General Constructor
    TreeNode(String s, TreeNode parent) {
        // Create a new array to store data
        data = new int[0];
        // If the specified String is not null...
        if (s != null) {
            // Attempt to add the String to the dictionary, get the index used
            // to retrived the String from the dictionary, and add it to "data"
            int index = Dictionary.add(s);
            addToData(index);
        }
        // Set all other fields to their corresponding default values
        this.parent = parent;
        children = new TreeNode[0];
        frequency = 0;
    }

    /**
     * Attempts to add a new child node to this node
     * @param child The node to be added as a child of this node
     * @return The inputed node if successfully added. If an equal node
     *         already exists as a child of this node, returns that node
     */
    public TreeNode addChild(TreeNode child) {
        // For each child of this node...
        for (TreeNode t : children) {
            // If the new node equals this child, return the corresponding child
            if (t.isEqual(child)) {
                return t;
            }
        }
        // If the method reaches this point, the new child is not equal
        // to any current child. Add the new child and return it
        addToChildren(child);
        return child;
    }

    // Merges this node with its child
    // (Note: Should only be called on nodes with one child, with frequency 0)
    public void mergeChild() {
        // Adds the data of the children to this node
        addToData(children[0].data);
        // The frequency of this node should now be the frequency of the child
        frequency = children[0].frequency;
        for (TreeNode tn : children[0].children) {
            tn.parent = this;
        }
        // The children of this node should now be the children of the child
        children = children[0].children;
    }

    // Increments "frequency"
    public void incrementFrequency() {  frequency++;  }

    public void updateWeight() {
        weight = frequency;
    }

    // Increments "passingFrequency"
    public void incrementPassingFrequency() { passingFrequency++; }

    /**
     * Adds a new node to the list of this node's children
     * @param child The child to be added to this nodes list of children
     */
    public void addToChildren(TreeNode child) {
        // Create a new array to store all previous children plus the new child
        TreeNode[] newArray = new TreeNode[children.length + 1];
        // Offset serves the dual purpose of tracking if the new child has been
        // inserted, while also assisting with the insertion of previously existing children
        int offset = 0;
        // For each child in the previous child array...
        for (int i = 0; i < children.length; i++) {
            // If the new child has not been inserted but should be inserted at this index...
            if (offset == 0 && child.toString().compareTo(children[i].toString()) < 0) {
                // Insert the child and update offset
                newArray[i] = child;
                offset = 1;
            }
            // Insert the previous child for this iteration based on offset
            newArray[i + offset] = children[i];
        }
        // If the child was never inserted, insert the child at the end of the list
        if (offset == 0) newArray[children.length] = child;

        // Replace the previous child array with the new one
        children = newArray;
    }

    /**
     * Adds a single new index to the data array
     * @param index The index to be added
     */
    public void addToData(int index) {
        // Create a new array to store all the previous data plus the new data
        int[] newArray = new int[data.length + 1];
        // Add the data from the previous data array to the new data array
        for (int i = 0; i < data.length; i++) { newArray[i] = data[i];  }
        // Add the new data to the end of the new data array
        newArray[data.length] = index;
        // Replace the previoius data array with the new one
        data = newArray;
    }

    /**
     * Adds a list of indices to the data array
     * @param indices The list of indices to be added
     */
    public void addToData(int[] indices) {
        // Create a new array to store all the previous data plus the new data
        int[] newArray = new int[data.length + indices.length];
        // Add the data from the previous data array to th new data array
        for (int i = 0; i < data.length; i++) { newArray[i] = data[i]; }
        // Add the new data points to the end of the new data array
        for (int i = 0; i < indices.length; i++) { newArray[i + data.length] = indices[i]; }
        // Replace the previous data array with the new one
        data = newArray;
    }

    /**
     * Returns whether or not the data within two nodes is equal
     * @param other The node to be compared with this node
     * @return True of the data within the two nodes is equal, otherwise false
     */
    public boolean isEqual(TreeNode other) {
        // Get the data array from the other node
        int[] otherData = other.data;
        // If the lengths of the arrays are not equal, return false
        if (data.length != otherData.length) return false;
        // For each index in "data"...
        for (int i = 0; i < data.length; i++) {
            // If the values at this in both arrays are not equal, return false
            if (data[i] != otherData[i]) return false;
        }
        // If the methd reaches this point, the arrays
        // contain no differences. Return true
        return true;
    }

    public void setUsed() { used = true; }

    // Getter methods
    public int[] getDataArray() {  return data; }
    public int getFrequency() { return frequency; }
    public int getPassingFrequency() { return passingFrequency; }
    public TreeNode getParent() { return parent; }
    public TreeNode[] getChildren() { return children; }
    public boolean isUsed() { return used; }
    public float getWeight() { return weight; }

    /**
	 * Returns the data stored within this class as a formatted String
	 * @return The data stored within this class as a formatted String
	 */
    public String toString() {
        // Create a String to store the String that will be outputted
        String output = "";
        // For each value in "data"...
        for (int i = 0; i < data.length; i++) {
            // Add its corresponding word in the Dictionary to the String
            output += Dictionary.get(data[i]);
            // If there are more data points to add to the
            // String, add a space to separate them
            if (i < data.length - 1) output += " ";
        }

        // Return the resulting String
        return output;
    }

    public String getSearchPhrase() {
        if (frequency == 0) return null;
        String phrase = toString();
        TreeNode currentNode = parent;
        while (currentNode.parent != null) {
            phrase = currentNode.toString() + " " + phrase;
            currentNode = currentNode.parent;
        }
        return phrase;
    }
}
