package learn.validate;

import model.ILexicalEntity;

/**
 *
 * Represents a classification result.
 *
 * @author Cedric Richter
 */
public interface IClassificationTestResult {

    /**
     *
     * @return the entity which was used for the prediction
     */
    public ILexicalEntity getEntity();

    /**
     *
     * @return the prediction of the classifier
     */
    public String getPredictedRelation();

    /**
     *
     * @return the real label of the entity
     */
    public String getValidRelation();

}
