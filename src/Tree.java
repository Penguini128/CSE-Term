import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Tree {

    private TreeNode root;
    private boolean hasBeenCompressed = false;

    Tree() {
        root = new TreeNode(null, null);
    }

    public TreeNode addNode(String data, TreeNode parent) {
        if (parent == null) parent = root;
        TreeNode newNode = new TreeNode(data, parent);
        newNode = parent.addChild(newNode);
        return newNode;
    }

    public void compress(TreeNode startNode) {
        if (startNode == null) startNode = root;
        for (TreeNode t : startNode.getChildren()) {
            compress(t);
        }
        if (startNode.getFrequency() == 0 && startNode.getChildren().length == 1) {
            startNode.mergeData(startNode.getChildren()[0]);
        }
        hasBeenCompressed = true;
    }

    public TreeNode getRoot() { return root; }

    public String toString() {
        return generateString(root, new ArrayList<Boolean>());
    }

    private String generateString(TreeNode startingNode, ArrayList<Boolean> includeLines) {
        StringBuilder sb = new StringBuilder();
        includeLines.add(true);
        for (int i = 0; i < startingNode.getChildren().length; i++) {
            TreeNode t = startingNode.getChildren()[i];
            /*/
            for (int j = 0; j < includeLines.size(); j++) {
                if (includeLines.get(j)) {
                    sb.append("|    ");
                } else sb.append("     ");
            }
            sb.append("\n");
            */
            for (int j = 0; j < includeLines.size(); j++) {
                if (includeLines.get(j)) {
                    if (j == includeLines.size() - 1) sb.append("|----");
                    else sb.append("|    ");
                } else sb.append("     ");
            }
            if (i == startingNode.getChildren().length - 1) includeLines.set(includeLines.size() - 1, false);
            sb.append(t.toString());
            sb.append(" (");
            sb.append(t.getPassingFrequency());
            sb.append(" / ");
            sb.append(t.getFrequency());
            sb.append(")\n");
            sb.append(generateString(t, includeLines));
        }
        includeLines.remove(includeLines.size() - 1);
        return sb.toString();
    }

    public void writeToFile(String filename) {
        String treeString = toString();

        String treeFileName = filename.substring(0, filename.length() - 4);
        if (hasBeenCompressed) treeFileName += "CompressedTree.txt";
        else treeFileName += "Tree.txt";
        FileWriter fw;
        try {
            fw = new FileWriter(treeFileName);
            fw.write(treeString);
            fw.close();
        } catch (IOException e) {
            System.out.println("ERROR:\t Unable to save tree representation of old queries to \"" + treeFileName + "\"");
        }
    }

}
