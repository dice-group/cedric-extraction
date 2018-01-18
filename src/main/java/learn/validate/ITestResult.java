package learn.validate;

import java.util.List;
import java.util.Map;

public interface ITestResult {

    public List<ITestScorer> getScores();

    public Map<String, Double> getResults();

}
