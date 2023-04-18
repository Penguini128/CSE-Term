import java.util.ArrayList;

public class TrieTable {

    private final ArrayList<String> table = new ArrayList<>();

    private final ArrayList<Weight> weights = new ArrayList<>();

    public TrieTable() {
    }

    public int add(String query) {
        table.add(query);
        weights.add(new Weight(query.length(), 1));
        return table.size() - 1;
    }

    // getter and setter

    public String get(int index) {
        return table.get(index);
    }

    public String get(int index, int start, int end) {
        return table.get(index).substring(start, end);
    }

    public Weight getWeight(int index) {
        return weights.get(index);
    }

    // DEBUG REMOVE BEFORE SUBMISSION
    public void printTable() {
        System.out.println('=' * 80);
        System.out.println("TrieTable:");
        for (String s : table) {
            System.out.println(s);
        }
        System.out.println('=' * 80);
    }

}
