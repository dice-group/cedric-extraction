package learn.validate.simple;

import learn.validate.ITestResult;
import learn.validate.ITestScorer;

import java.util.List;
import java.util.Map;

public class SimpleTestResult implements ITestResult{

    private List<ITestScorer> scores;
    private Map<String, Double> results;

    public SimpleTestResult(List<ITestScorer> scores, Map<String, Double> results) {
        this.scores = scores;
        this.results = results;
    }


    @Override
    public List<ITestScorer> getScores() {
        return scores;
    }

    @Override
    public Map<String, Double> getResults() {
        return results;
    }
}
