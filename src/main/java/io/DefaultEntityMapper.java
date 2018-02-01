package io;

import com.google.common.collect.ImmutableList;
import edu.stanford.nlp.simple.Sentence;
import model.ILabelledEntity;
import model.SimpleLabelledEntity;
import org.bson.BSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A default mapper for MongoDB entries.
 * The MongoDB entries have to follow the following convention:
 *
 *  {
 *      "predicate": STR,
 *      "source": STR,
 *      "sourceLabel": STR,
 *      "sourcePosition": INT,
 *      "sentence": STR,
 *      "target": STR,
 *      "targetLabel": STR,
 *      "targetPosition": INT
 *  }
 *
 *
 * @author Cedric Richter
 */
public class DefaultEntityMapper implements IEntityMapper {


    @Override
    public ILabelledEntity mapEntity(BSONObject object) {

        String first, second;

        if((int)object.get(MongoConstants.SOURCE_POS) > (int)object.get(MongoConstants.TARGET_POS)){
            first = (String) object.get(MongoConstants.TARGET_LABEL);
            second = (String) object.get(MongoConstants.SOURCE_LABEL);
        }else{
            first = (String) object.get(MongoConstants.SOURCE_LABEL);
            second = (String) object.get(MongoConstants.TARGET_LABEL);
        }

        String predicate = (String)object.get(MongoConstants.PREDICATE);

        List<List<String>> split = sentenceSplit(object);

        ImmutableList<String> front = ImmutableList.copyOf(split.get(0));
        ImmutableList<String> inner = ImmutableList.copyOf(split.get(1));
        ImmutableList<String> back = ImmutableList.copyOf(split.get(2));

        return new SimpleLabelledEntity(first, second, predicate, front, inner, back);
    }


    private List<List<String>> sentenceSplit(BSONObject obj){
        String str = (String) obj.get(MongoConstants.SENTENCE);

        Sentence sentence = new Sentence(str);

        String src = (String) obj.get(MongoConstants.SOURCE);
        String target = (String) obj.get(MongoConstants.TARGET);

        if((int)obj.get(MongoConstants.SOURCE_POS) > (int)obj.get(MongoConstants.TARGET_POS)){
            String tmp = src;
            src = target;
            target = tmp;
        }

        List<List<String>> out = new ArrayList<>();
        List<String> tmp = new ArrayList<>();

        for(String s: sentence.words()){
            if(src.contains(s)|| target.contains(s)){
                if(!tmp.isEmpty()) {
                    out.add(tmp);
                    tmp = new ArrayList<>();
                }
            }else {
                tmp.add(s);
            }
        }

        return out;
    }


}
