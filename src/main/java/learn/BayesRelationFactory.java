package learn;

import model.ITrainingData;

import java.io.IOException;
import java.util.HashMap;

public class BayesRelationFactory implements IRelationClassificatorFactory {

    private IRCSearchSpaceFactory factory;

    public BayesRelationFactory(IRCSearchSpaceFactory factory) {
        this.factory = factory;
    }


    @Override
    public IRelationClassificator trainModel(Iterable<ITrainingData> data) {

        RCModel model = new RCModel(new HashMap<>(), factory);

        for(ITrainingData d: data)
            model.addData(d);

        return new BayesRelationClassificator(model);
    }

    @Override
    public IRelationClassificator loadModel(String path) throws IOException {
        return null;
    }
}
