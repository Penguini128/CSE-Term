public class TreeNode {

    private int[] data;
    private int frequency;
    private int passingFrequency;
    private TreeNode parent;
    private TreeNode[] children;

    TreeNode(String s, TreeNode parent) {
        data = new int[0];
        if (s != null) {
            int index = Dictionary.add(s);
            addToData(index);
        }
        this.parent = parent;
        children = new TreeNode[0];
        frequency = 0;
    }

    public TreeNode addChild(TreeNode child) {
        for (TreeNode t : children) {
            if (t.dataEquals(child.data)) {
                return t;
            }
        }
        addToChildren(child);
        return child;
    }

    public void mergeData(TreeNode child) {
        addToData(child.data);
        frequency += child.frequency;
        children = child.children;
    }

    public void incrementFrequency() {
        frequency++;
    }

    public void incrementPassingFrequency() {
        passingFrequency++;
    }

    public void addToChildren(TreeNode child) {
        TreeNode[] newArray = new TreeNode[children.length + 1];
        int insertedIndex = children.length;
        for (int i = 0; i < children.length; i++) {
            if (child.toString().compareTo(children[i].toString()) < 0) {
                insertedIndex = i;
                break;
            } else {
                newArray[i] = children[i];
            }
        }
        newArray[insertedIndex] = child;
        for (int i = insertedIndex; i < children.length; i++) {
            newArray[i+1] = children[i];
        }
        children = newArray;
    }

    public void addToData(int index) {
        int[] newArray = new int[data.length + 1];

        for (int i = 0; i < data.length; i++) {
            newArray[i] = data[i];
        }
        newArray[newArray.length - 1] = index;
        data = newArray;
    }

    public void addToData(int[] indices) {
        int[] newArray = new int[data.length + indices.length];

        for (int i = 0; i < data.length; i++) {
            newArray[i] = data[i];
        }
        for (int i = data.length; i < newArray.length; i++) {
            newArray[i] = indices[i - data.length];
        }
        data = newArray;
    }

    public boolean dataEquals(int[] otherData) {
        if (data.length != otherData.length) return false;
        for (int i = 0; i < data.length; i++) {
            if (data[i] != otherData[i]) return false;
        }
        return true;
    }

    public int[] getDataArray() {  return data; }
    public int getFrequency() { return frequency; }
    public int getPassingFrequency() { return passingFrequency; }
    public TreeNode getParent() { return parent; }
    public TreeNode[] getChildren() { return children; }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            sb.append(Dictionary.get(data[i]));
            if (i < data.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
