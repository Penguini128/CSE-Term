public class PhraseNode {

    private short[] data;
    private short frequency;
    private boolean used;

    // General Constructor
    PhraseNode(short[] data) {
        this.data = data;
        frequency = 1;
        used = false;
    }

    // Increments frequency
    public void incrementFrequency() { frequency++; }

    // Getter methods
    public short[] getDataArray() { return data; }
    public short getFrequency() { return frequency; }
    public void setUsed() { used = true; }
    public boolean isUsed() { return used; }

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