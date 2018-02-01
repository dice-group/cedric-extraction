package model;

/**
 * A base configuration for creating a relation classifier
 *
 * @author Cedric Richter
 */
public interface IConfiguration {

    /**
     *
     * @return how many features before the first entity and behind the last entity should be selected
     */
    public int getLexicalWindow();

    /**
     *
     * @return the bayes smoothing factor for unknown features
     */
    public double getBayesSmoothing();

    /**
     *
     * @return the number of selected nearest neighbours
     */
    public int getNeighbour();

}
