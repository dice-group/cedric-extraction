package model;

/**
 * A model for a relation between two entities
 *
 * @author Cedric Richter
 */
public class Relation {

    private RelationEntity subject, object;
    private String relation;

    public Relation(RelationEntity subject, RelationEntity object, String relation) {
        this.subject = subject;
        this.object = object;
        this.relation = relation;
    }

    public RelationEntity getSubject() {
        return subject;
    }

    public RelationEntity getObject() {
        return object;
    }

    public String getRelation() {
        return relation;
    }


}
