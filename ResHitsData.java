
public class ResHitsData implements Comparable<ResHitsData> {

    private String ID;
    private double rank;

    public ResHitsData(String ID, double authority) {
        this.ID = ID;
        this.rank = authority;
    }

    public double getRank() {
        return rank;
    }

    public String getID() {
        return ID;
    }

    @Override
    public int compareTo(ResHitsData resHitsData) {

        // this authority is larger than hitsData's authority
        if (this.getRank() - resHitsData.getRank() > Search.threshold)
            return -1;

        // this authority is smaller than hitsData's authority
        if (this.getRank() - resHitsData.getRank() < -Search.threshold)
            return 1;

        // this authority is about the same as hitsData's authority
        return 0;
    }

    @Override
    public String toString() {
        return "ID = '" + ID + '\'' +
                ", rank = " + rank +
                '}' + "\n";
    }
}