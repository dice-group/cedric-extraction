package model;

import java.util.Map;

/**
 * Represents a lexical feature
 *
 * @author Cedric Richter
 */
public interface ILexicalFeature {

    /**
     *
     * @return the feature selected from a sentence
     */
    public String getFeature();

    /**
     * Additional properties of this feature. The map can be read and modified.
     * @return a map of properties
     */
    public Map<String, Object> getProperties();
}
