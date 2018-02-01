package learn.validate;


/**
 *
 * This score calculates the combined precision independent of the used relation.
 *
 * @author Cedric Richter
 */
public class PerformanceScorer implements ITestScorer{

    private int count;
    private int pos;

    @Override
    public void process(IClassificationTestResult result) {
        count ++;
        pos += (result.getPredictedRelation().equals(result.getValidRelation())?1:0);
    }

    @Override
    public double getScoreAndReset() {
        double out = (double)pos/count;

        pos = 0;
        count = 0;

        return out;
    }

    @Override
    public String getScoreName() {
        return "Performance";
    }
}
