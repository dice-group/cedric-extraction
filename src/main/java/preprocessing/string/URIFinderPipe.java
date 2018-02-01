package preprocessing.string;


import javafx.util.Pair;


import model.NEREntity;
import model.RelationEntity;
import org.aksw.fox.data.Entity;
import pipeline.APipe;


import java.util.*;

/**
 * A pipe which link the given entities to URIs of an web ontology
 * Based on:
 * {@see https://github.com/dice-group/FOX/blob/master/src/main/java/org/aksw/fox/Fox.java}
 *
 * @author Cedric Richter
 */
public class URIFinderPipe extends APipe<Pair<NEREntity, NEREntity>, Pair<RelationEntity, RelationEntity>> {
    private Agdistis linking;

    public URIFinderPipe(){
        init();
    }

    private void init(){
        linking = new Agdistis();
    }

    private Set<Entity> index(Set<Entity> entities, String input){
        TokenManager tokenManager = new TokenManager(input.toLowerCase());

        tokenManager.repairEntities(entities);

        // make an index for each entity
        Map<Integer, Entity> indexMap = new HashMap<>();
        for (Entity entity : entities)
            for (Integer i : TextUtil.getIndices(entity.getText(), tokenManager.getTokenInput()))
                indexMap.put(i, entity);

        // sort
        List<Integer> sortedIndices = new ArrayList<>(indexMap.keySet());
        Collections.sort(sortedIndices);

        // loop index in sorted order
        int offset = -1;
        for (Integer i : sortedIndices) {
            Entity e = indexMap.get(i);
            if (offset < i) {
                offset = i + e.getText().length();
                e.addIndicies(i);
            }
        }

        // remove entity without an index
        {
            Set<Entity> cleanEntity = new HashSet<>();
            for (Entity e : entities) {
                if (e.getIndices() != null && e.getIndices().size() > 0) {
                    cleanEntity.add(e);
                }
            }
            entities = cleanEntity;
        }
        return entities;
    }


    public Pair<RelationEntity, RelationEntity> addURI(Pair<NEREntity, NEREntity> entity){

        NEREntity entity1 = entity.getKey();
        NEREntity entity2 = entity.getValue();

        Set<Entity> entities = new HashSet<>();
        Entity e1 = new Entity(entity1.getName(), entity1.getLabel());
        entities.add(e1);
        Entity e2 = new Entity(entity2.getName(), entity2.getLabel());
        entities.add(e2);

        entities = index(entities, entity1.getContext());

        linking.setUris(entities, entity1.getContext());

        return new Pair<>(new RelationEntity(entity1, e1.uri), new RelationEntity(entity2, e2.uri));
    }



    @Override
    public void push(Pair<NEREntity, NEREntity> obj) {
        sink.push(addURI(obj));
    }
}
