
public class ItemData implements Comparable<ItemData> {

    private String name;
    private double score;

    public ItemData(String name, double score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public double getScore() {
        return score;
    }

    @Override
    public int compareTo(ItemData o) {
        double delta = this.getScore() - o.getScore();

        if (delta > 0)
            return -1;

        if (delta < 0)
            return 1;

        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: " + name);
        sb.append("\tScore: " + score);
        return sb.toString();
    }
}
