public class PhraseNode {
    private int[] data;
    private int frequency;
    private boolean used;
    public PhraseNode next;

    PhraseNode(int[] data) {
        this.data = data;
        frequency = 1;
        used = false;
    }
    public void incrementFrequency() { frequency++; }

    public int[] getDataArray() { return data; }
    public int getFrequency() { return frequency; }
    public void setUsed() { used = true; }
    public boolean isUsed() { return used; }

    public boolean equals(PhraseNode other) {
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

    public String toString() {
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
}