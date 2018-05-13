package org.dice_research.cedric_extraction.learn;

import com.google.common.collect.Multimap;
import org.dice_research.cedric_extraction.model.ITrainingData;

/**
 * A search space to find features with a matching classification
 *
 * @author Cedric Richter
 */
public interface IRCSearchSpace {

    /**
     * It allows to perform a search on multiple features
     * @param features a stream of features
     * @return a mapping of features to a collection of search results
     */
    public Multimap<String, IRCSearchResult> search(Iterable<String> features);

    /**
     * Extend the existing search space by the given training data.
     * @param data training entity to include in search space
     */
    public void extend(ITrainingData data);

}
