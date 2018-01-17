package learn;

import model.ITrainingData;

import java.io.IOException;
import java.util.HashMap;

public class VotingRelationFactory implements IRelationClassificatorFactory {

    private IRCSearchSpaceFactory factory;

    public VotingRelationFactory(IRCSearchSpaceFactory factory) {
        this.factory = factory;
    }


    @Override
    public IRelationClassificator trainModel(Iterable<ITrainingData> data) {

        RCModel model = new RCModel(new HashMap<>(), factory);

        for(ITrainingData d: data)
            model.addData(d);

        return new VotingRelationClassificator(model);
    }

    @Override
    public IRelationClassificator loadModel(String path) throws IOException {
        return null;
    }
}
