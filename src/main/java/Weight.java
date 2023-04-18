public class Weight {

    private int length;

    private int frequency;

    public Weight(int length, int frequency) {
        this.length = length;
        this.frequency = frequency;
    }

    public int compare(Weight w) {
        if (this.length < w.length) {
            return 1;
        } else if (this.length > w.length) {
            return -1;
        }

        if (this.frequency > w.frequency) {
            return 1;
        } else if (this.frequency < w.frequency) {
            return -1;
        }

        return 0;
    }

    /**
     * getters and setters
     */

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFrequency() {
        return frequency;
    }

    public void incrementFrequency() {
        this.frequency++;
    }

}
