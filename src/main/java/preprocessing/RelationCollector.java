package preprocessing;

import model.ILabelledEntity;
import pipeline.APipe;

import java.util.HashSet;
import java.util.Set;

public class RelationCollector extends APipe<ILabelledEntity, ILabelledEntity>{

    private Set<String> relations = new HashSet<>();

    public Set<String> getRelations(){
        return relations;
    }

    @Override
    public void push(ILabelledEntity obj) {
        relations.add(obj.getPredicate());
        sink.push(obj);
    }
}
