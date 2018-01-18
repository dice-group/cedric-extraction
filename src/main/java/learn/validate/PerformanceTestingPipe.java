package learn.validate;

import learn.validate.simple.SimpleTestResult;
import pipeline.APipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformanceTestingPipe extends APipe<IClassificationTestResult, ITestResult> {

    public static final String STOP_SIGNAL = "performance";

    private List<ITestScorer> scores;

    public PerformanceTestingPipe(List<ITestScorer> scores) {
        this.scores = scores;
    }


    @Override
    public void push(IClassificationTestResult obj) {

        for(ITestScorer score: scores)
            score.process(obj);


    }

    @Override
    public void stopSignal(String type){

        Map<String, Double> result = new HashMap<>();

        for(ITestScorer score: scores)
            result.put(score.getScoreName(), score.getScoreAndReset());

        sink.push(new SimpleTestResult(scores, result));

        sink.stopSignal(PerformanceTestingPipe.STOP_SIGNAL);

    }
}
