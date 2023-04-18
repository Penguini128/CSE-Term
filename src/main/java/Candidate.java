public class Candidate {

    private final String query;

    private final Weight weight;

    public Candidate(String query, Weight weight) {
        this.query = query;
        this.weight = weight;
    }

    public String getQuery() {
        return query;
    }

    public Weight getWeight() {
        return weight;
    }

}
