package learn.validate;

public class F1Score implements ITestScorer{

    private int truePos = 0;
    private int falsePos = 0;
    private int falseNeg = 0;

    private String relation;

    public F1Score(String relation) {
        this.relation = relation;
    }

    @Override
    public void process(IClassificationTestResult result) {

        if(result.getValidRelation().equals(relation)){
            boolean pos = result.getValidRelation().equals(result.getPredictedRelation());

            truePos += pos?1:0;
            falseNeg += pos?0:1;
        }else if(result.getPredictedRelation().equals(relation)){
            falsePos++;
        }

    }

    @Override
    public double getScoreAndReset() {

        double precision = (double)truePos/(truePos + falsePos);
        double recall = (double)truePos / (truePos + falseNeg);

        truePos = 0;
        falsePos = 0;
        falseNeg = 0;

        return 2 * (precision * recall) / (precision + recall);
    }

    @Override
    public String getScoreName() {
        return "F1 ( "+relation+" )";
    }
}
