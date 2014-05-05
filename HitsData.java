
public class HitsData implements Comparable<HitsData> {

    private String ID;
    private double hubness;
    private double authority;


    public HitsData(String url) {
        ID = url;
        hubness = 1.0;
        authority = 1.0;
    }

    public HitsData(String ID, double hubness, double authority) {
        this.ID = ID;
        this.hubness = hubness;
        this.authority = authority;
    }

    public void setAuthority(double authority) {
        this.authority = authority;
    }

    public void setHubness(double hubness) {

        this.hubness = hubness;
    }

    public double getHubness() {
        return hubness;
    }

    public double getAuthority() {
        return authority;
    }


    @Override
    public int compareTo(HitsData hitsData) {

        // this authority is larger than hitsData's authority
        if (this.getAuthority() - hitsData.getAuthority() > Search.threshold)
            return -1;

        // this authority is smaller than hitsData's authority
        if (this.getAuthority() - hitsData.getAuthority() < -Search.threshold)
            return 1;

        // this authority is about the same as hitsData's authority
        return 0;
    }

    @Override
    public String toString() {
        return "HitsData{" +
                "ID='" + ID + '\'' +
                ", authority=" + authority +
                '}' + "\n";
    }
}


