package learn;

import model.ITrainingData;

import java.io.IOException;
import java.util.HashMap;

/**
 * A factory to create voting classifier for relation extraction
 *
 * @author Cedric Richter
 */
public class VotingRelationFactory implements IRelationClassifierFactory {

    private IRCSearchSpaceFactory factory;

    public VotingRelationFactory(IRCSearchSpaceFactory factory) {
        this.factory = factory;
    }

    @Override
    public IRelationClassifier trainModel(Iterable<ITrainingData> data) {
        RCModel model = new RCModel(new HashMap<>(), factory);

        for(ITrainingData d: data)
            model.addData(d);

        return new VotingRelationClassifier(model);
    }

    @Override
    public IRelationClassifier loadModel(String path) throws IOException {
        throw new UnsupportedOperationException();
    }
}
