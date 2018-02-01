package learn;

import model.ITrainingData;

import java.io.IOException;

/**
 *
 * A factory for creating new relation classifier
 *
 * @author Cedric Richter
 */
public interface IRelationClassifierFactory {

    /**
     * Train a new model on the stream of training data
     * @param data labelled data for training a model
     * @return a trained classifier
     */
    public IRelationClassifier trainModel(Iterable<ITrainingData> data);

    /**
     * Load a classifier which is saved to a file
     * @param path the path to the file containing the classifier
     * @return a loaded classifier
     * @throws IOException if the file cannot be accessed
     */
    public IRelationClassifier loadModel(String path) throws IOException;

}
