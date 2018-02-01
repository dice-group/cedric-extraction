package bayes;

/**
 *
 * A category for training
 *
 * @author Cedric Richter
 */
public interface ICategory {

    /**
     *
     * @return the first label of this category
     */
    public String getFirstLabel();


    /**
     *
     * @return the second label for this category
     */
    public String getSecondLabel();


    /**
     *
     * @return the predicate for the category
     */
    public String getPredicate();

}
