package org.dice_research.cedric_extraction.learn.validate.simple;

import org.dice_research.cedric_extraction.learn.validate.ITestResult;
import org.dice_research.cedric_extraction.learn.validate.ITestScorer;

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
