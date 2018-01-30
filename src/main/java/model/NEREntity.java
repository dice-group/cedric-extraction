package model;

public class NEREntity {

    private String context;
    private String name;
    private String label;

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
