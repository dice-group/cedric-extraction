package learn.validate;

public interface ITestScorer {

    public void process(IClassificationTestResult result);

    public double getScoreAndReset();

    public String getScoreName();

}
