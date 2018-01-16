package learn;

import bayes.IBayesEstimator;

public class RCDefaultFactory implements IRCSearchSpaceFactory{

    private int k;
    private IBayesEstimator estimator;

    public RCDefaultFactory(int k, IBayesEstimator estimator) {
        this.k = k;
        this.estimator = estimator;
    }

    @Override
    public IRCSearchSpace create() {
        return new RCDefaultSpace(k, estimator);
    }
}
