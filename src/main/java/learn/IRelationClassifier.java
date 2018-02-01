package learn;

import model.ILexicalEntity;

import java.io.IOException;

/**
 * A classifier which can predict a relation for given entities.
 *
 * @author Cedric Richter
 */
public interface IRelationClassifier {

    /**
     * Predict a relation for a given entity.
     * If the classifier did not find any explicit relation the return value will be "unknown"
     * @param entity a entity which should be classified
     * @return the predicted relation or unknown
     */
    public String predict(ILexicalEntity entity);

    /**
     * Saves the classifier model to a given path
     * @param path path to save
     * @throws IOException if the file cannot be opened
     */
    public void save(String path) throws IOException;

}
