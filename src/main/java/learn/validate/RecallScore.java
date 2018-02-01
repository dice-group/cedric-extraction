package learn.validate;

/**
 *
 * Calculates the recall for a single relation.
 *
 * Recall:
 *      (True Positive) / (True Positive + False Negative)
 *
 * @author Cedric Richter
 */
public class RecallScore implements ITestScorer {
    private int truePos = 0;
    private int falseNeg = 0;

    private String relation;

    public RecallScore(String relation) {
        this.relation = relation;
    }

    @Override
    public void process(IClassificationTestResult result) {

        if(result.getValidRelation().equals(relation)){
            boolean pos = result.getValidRelation().equals(result.getPredictedRelation());

            truePos += pos?1:0;
            falseNeg += pos?0:1;
        }

    }

    @Override
    public double getScoreAndReset() {

        double recall = (double)truePos / (truePos + falseNeg);

        truePos = 0;
        falseNeg = 0;

        return recall;
    }

    @Override
    public String getScoreName() {
        return "Recall ( "+relation+" )";
    }
}
