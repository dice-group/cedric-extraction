package model;

import com.google.common.collect.ImmutableList;

/**
 * A lexical entity represents the selected features and entity labels of one sentence.
 *
 * @author Cedric Richter
 */
public interface ILexicalEntity {

    /**
     *
     * @return the label of the entity occurring earlier in the sentence
     */
    public String getFirstLabel();

    /**
     *
     * @return the label of the entity occurring later in the sentence
     */
    public String getSecondLabel();

    /**
     *
     * @return features which are positioned before the first entity
     */
    public ImmutableList<String> getFrontWindow();

    /**
     *
     * @return features which are positioned after the last entity
     */
    public ImmutableList<String> getBackWindow();

    /**
     *
     * @return features which are positioned between the two entities
     */
    public ImmutableList<String> getInnerFeature();

}
