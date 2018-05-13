package org.dice_research.cedric_extraction.preprocessing;

import org.dice_research.cedric_extraction.model.Relation;
import org.dice_research.cedric_extraction.pipeline.APipe;

/**
 * Sometimes relation are used in learning without prefix.
 * This pipe add http://dbpedia.org/ontology/ as a prefix to the relation URI.
 *
 * @author Cedric Richter
 */
public class RelationPrefixPostprocessor extends APipe<Relation, Relation> {
    @Override
    public void push(Relation obj) {
        if(!obj.getRelation().equals("unknown")){
            sink.push(new Relation(obj.getSubject(), obj.getObject(), "http://dbpedia.org/ontology/"+obj.getRelation()));
        }else{
            sink.push(obj);
        }
    }
}
