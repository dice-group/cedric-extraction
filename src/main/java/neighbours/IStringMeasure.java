package neighbours;

public interface IStringMeasure {

    public boolean inFilter(String s1, String s2, double minDist);

    public double getDistance(String s1, String s2);

}
