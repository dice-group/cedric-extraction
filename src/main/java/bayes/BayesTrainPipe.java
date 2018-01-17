package bayes;

import model.ILabelledEntity;
import pipeline.APipe;

public class BayesTrainPipe extends APipe<ILabelledEntity, ILabelledEntity> {

    private IBayesEstimator estimator;

    public BayesTrainPipe(IBayesEstimator estimator) {
        this.estimator = estimator;
    }

    @Override
    public void push(ILabelledEntity obj) {
        estimator.train(obj);
        sink.push(obj);
    }

}
