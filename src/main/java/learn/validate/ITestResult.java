package learn.validate;

import java.util.List;
import java.util.Map;

/**
 *
 * Represents a test result
 *
 * @author Cedric Richter
 */
public interface ITestResult {

    /**
     *
     * @return the list of used scores
     */
    public List<ITestScorer> getScores();

    /**
     * After getting a score from the used function the function will be reset.
     * Therefore the test scores are saved to this map.
     *
     * @return a map of all test scores
     */
    public Map<String, Double> getResults();

}
