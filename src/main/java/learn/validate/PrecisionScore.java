package learn.validate;

/**
 *
 * Calculates the precision for a single relation.
 *
 * Precision:
 *      (True Positive) / (True Positive + False Positive)
 *
 * @author Cedric Richter
 */
public class PrecisionScore implements ITestScorer {
    private int truePos = 0;
    private int falsePos = 0;


    private String relation;

    public PrecisionScore(String relation) {
        this.relation = relation;
    }

    @Override
    public void process(IClassificationTestResult result) {

        if(result.getValidRelation().equals(relation)){
            boolean pos = result.getValidRelation().equals(result.getPredictedRelation());

            truePos += pos?1:0;
        }else if(result.getPredictedRelation().equals(relation)){
            falsePos++;
        }

    }

    @Override
    public double getScoreAndReset() {

        double precision = (double)truePos/(truePos + falsePos);

        truePos = 0;
        falsePos = 0;

        return precision;
    }

    @Override
    public String getScoreName() {
        return "Precision ( "+relation+" )";
    }
}
