package model;

/**
 * An entity which is found and labelled in a sentence by a NER approach.
 *
 * @author Cedric Richter
 */
public class NEREntity {

    private String context;
    private String name;
    private String label;

    /**
     *
     * @param context the sentence where the entity comes from
     * @param name the name of the entity
     * @param label the NER label of the entity
     */
    public NEREntity(String context, String name, String label) {
        this.context = context;
        this.name = name;
        this.label = label;
    }

    public String getContext() {
        return context;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }
}
