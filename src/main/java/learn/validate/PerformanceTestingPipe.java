package learn.validate;

import pipeline.APipe;

public class PerformanceTestingPipe extends APipe<IClassificationTestResult, Double> {

    public static final String STOP_SIGNAL = "performance";

    private int positive = 0;
    private int examples = 0;

    @Override
    public void push(IClassificationTestResult obj) {

        examples++;
        positive += (obj.getPredicatedRelation().equals(obj.getValidRelation()))?1:0;

    }

    @Override
    public void stopSignal(String type){

        sink.push((double)positive/examples);
        sink.stopSignal(PerformanceTestingPipe.STOP_SIGNAL);

    }
}
