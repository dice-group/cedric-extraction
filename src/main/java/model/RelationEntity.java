package model;

public class RelationEntity extends NEREntity {

    private String uri;

    public RelationEntity(String context, String name, String label, String uri) {
        super(context, name, label);
        this.uri = uri;
    }

    public RelationEntity(NEREntity entity, String uri) {
        super(entity.getContext(), entity.getName(), entity.getLabel());
        this.uri = uri;
    }

    public String getUri(){
        return uri;
    }
}
