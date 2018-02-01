package learn;

import model.ITrainingData;

import java.io.IOException;
import java.util.HashMap;

/**
 *
 * A factory for a bayes relation classifier
 *
 * @author Cedric Richter
 */
public class BayesRelationFactory implements IRelationClassifierFactory {

    private IRCSearchSpaceFactory factory;

    public BayesRelationFactory(IRCSearchSpaceFactory factory) {
        this.factory = factory;
    }


    @Override
    public IRelationClassifier trainModel(Iterable<ITrainingData> data) {

        RCModel model = new RCModel(new HashMap<>(), factory);

        for(ITrainingData d: data)
            model.addData(d);

        return new BayesRelationClassifier(model);
    }

    @Override
    public IRelationClassifier loadModel(String path) throws IOException {
        throw new UnsupportedOperationException();
    }
}
