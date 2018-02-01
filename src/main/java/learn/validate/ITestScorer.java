package learn.validate;

/**
 *
 * Represents a testing score.
 * The testing score can use state until the result is queried.
 * After this the class should loose its state.
 *
 * @author Cedric Richter
 */
public interface ITestScorer {

    /**
     * Process an incoming result. It saves the result
     * as input to the overall score.
     * @param result a result to process
     */
    public void process(IClassificationTestResult result);

    /**
     * Calculate and return the score. Then the class should reset its state.
     *
     * Undefined result for freshly reset state.
     *
     * @return the calculated score
     */
    public double getScoreAndReset();

    /**
     *
     * @return the name of this score
     */
    public String getScoreName();

}
