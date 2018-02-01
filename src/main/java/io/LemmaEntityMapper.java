package io;

import com.google.common.collect.ImmutableList;
import edu.stanford.nlp.simple.Sentence;
import model.ILabelledEntity;
import model.SimpleLabelledEntity;
import org.bson.BSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Same behaviour as {@see DefaultEntityMapper} but lemmatize the given features.
 *
 * @author Cedric Richter
 */
public class LemmaEntityMapper implements IEntityMapper {

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

        if(split == null)
            return null;

        ImmutableList<String> front = ImmutableList.copyOf(split.get(0));
        ImmutableList<String> inner = ImmutableList.copyOf(split.get(1));
        ImmutableList<String> back = ImmutableList.copyOf(split.get(2));

        return new SimpleLabelledEntity(first, second, predicate, front, inner, back);
    }


    private List<List<String>> sentenceSplit(BSONObject obj){
        String str = (String) obj.get(MongoConstants.SENTENCE);

        int srcPos = (int)obj.get(MongoConstants.SOURCE_POS);
        int srcLen = ((String)obj.get(MongoConstants.SOURCE)).length();

        int targetPos = (int)obj.get(MongoConstants.TARGET_POS);
        int targetLen = ((String)obj.get(MongoConstants.TARGET)).length();

        if(srcPos > targetPos){
            int tmp = srcPos;
            srcPos = targetPos;
            targetPos = tmp;

            tmp = srcLen;
            srcLen = targetLen;
            targetLen = tmp;
        }

        List<String> parts = new ArrayList<>();

        try {
            parts.add(str.substring(0, srcPos));
            parts.add(str.substring(srcPos + srcLen, targetPos));
            parts.add(str.substring(targetPos + targetLen));
        }catch(Exception e){
            return null;
        }

        List<List<String>> out = new ArrayList<>();

        for(String s: parts) {
            if(s.trim().isEmpty()){
                out.add(new ArrayList<>());
                continue;
            }
            Sentence sen = new Sentence(s);
            List<String> lemmas = new ArrayList<>();
            for(String lemma: sen.lemmas())
                lemmas.add(lemma.toLowerCase());
            out.add(lemmas);
        }

        return out;
    }


}
