package neighbours;

/**
 * A measure to calculate the distant between two strings
 *
 * @author Cedric Richter
 */
public interface IStringMeasure {

    /**
     * A fast filter which allows to preselect combination which satisfy a maximum distant.
     * @param s1 first string
     * @param s2 second string
     * @param minDist a maximum distant
     * @return true if the strings will have a lower distant than minDist
     */
    public boolean inFilter(String s1, String s2, double minDist);

    /**
     * Calculates the real distant between two strings
     * @param s1 first string
     * @param s2 second string
     * @return the distant between s1 and s2
     */
    public double getDistance(String s1, String s2);

}
