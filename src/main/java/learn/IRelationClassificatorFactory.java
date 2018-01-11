package learn;

import model.ITrainingData;

import java.io.IOException;
import java.util.Set;

public interface IRelationClassificatorFactory {

    public IRelationClassificator trainModel(Iterable<ITrainingData> data);

    public IRelationClassificator loadModel(String path) throws IOException;

}
