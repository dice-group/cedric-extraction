package model;

/**
 * The same as a lexical entity but it provides a label for the entity
 *
 * @author Cedric Richter
 */
public interface ILabelledEntity extends ILexicalEntity {

    /**
     *
     * @return the relation label for this entity
     */
    public String getPredicate();

}
