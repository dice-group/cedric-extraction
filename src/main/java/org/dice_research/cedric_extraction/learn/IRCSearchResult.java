package org.dice_research.cedric_extraction.learn;

import org.dice_research.cedric_extraction.model.ILexicalFeature;

/**
 * Represents a search result for a given feature
 *
 * @author Cedric Richter
 */
public interface IRCSearchResult {

    /**
     *
     * @return the feature which belongs to the classification
     */
    public ILexicalFeature getFeature();

    /**
     *
     * @return accuracy of the classification
     */
    public double getAccuracy();

    /**
     *
     * @return the classification for the used feature
     */
    public String getClassification();

}
