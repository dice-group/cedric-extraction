package bayes;

import model.ILabelledEntity;
import model.ILexicalEntity;

import java.util.Map;

/**
 *
 * An interface for Bayes estimation on lexical features
 *
 * Estimator is trained depended to entity labels.
 *
 * @author Cedric Richter
 */
public interface IBayesEstimator {

    /**
     * Train the bayes estimator by adding a new entity to the training set
     * @param entity a labelled entity for training
     */
    public void train(ILabelledEntity entity);

    /**
     * Estimate the probability for each feature in the given category
     * @param data the features for prediction
     * @param cat the category
     * @return a estimation for each feature.
     * @throws UnknownCategoryException returned if the category is never used in training
     */
    public Map<String, Double> estimate(Iterable<String> data, ICategory cat) throws UnknownCategoryException;

}
