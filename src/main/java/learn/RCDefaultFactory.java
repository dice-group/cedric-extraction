package learn;

import bayes.IBayesEstimator;

/**
 *
 * A default implementation for the RCSearchSpaceFactory
 *
 * @author Cedric Richter
 */
public class RCDefaultFactory implements IRCSearchSpaceFactory{

    private int k;
    private IBayesEstimator estimator;

    /**
     * Uses the given estimator and kNN model
     * @param k parameter for the kNN models
     * @param estimator the used estimator
     */
    public RCDefaultFactory(int k, IBayesEstimator estimator) {
        this.k = k;
        this.estimator = estimator;
    }

    @Override
    public IRCSearchSpace create() {
        return new RCDefaultSpace(k, estimator);
    }
}
