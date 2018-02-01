package preprocessing.string;

import com.google.common.collect.ImmutableList;
import edu.stanford.nlp.simple.Sentence;
import javafx.util.Pair;
import model.ILabelledEntity;
import model.NEREntity;
import model.SimpleLabelledEntity;
import pipeline.APipe;
import pipeline.ISink;

import java.util.ArrayList;
import java.util.List;

/**
 * A preprocessor which selects entities of a given string.
 * Then each combination of entities is created and a labelled entity is build
 *
 * @author Cedric Richter
 */
public class NERStringPreprocessor extends APipe<String, ILabelledEntity> {


    private ISink<Pair<NEREntity, NEREntity>> tagSink;

    /**
     * A sink which can be used to extract combinations of found NER entities
     * @param tagSink a sink which takes pairs of entities
     */
    public void setTagSink(ISink<Pair<NEREntity, NEREntity>> tagSink) {
        this.tagSink = tagSink;
    }

    @Override
    public void push(String obj) {

        Sentence sentence = new Sentence(obj);

        List<String> lemmas = sentence.lemmas();
        List<String> tags = sentence.nerTags();
        List<List<String>> tokens = tokenize(lemmas, tags);
        List<Integer> index = index(tokens);

        for(List<Integer> tuple: constructIndexTuple(index)){

            int f = tuple.get(0);
            int l = tuple.get(1);

            List<String> front = new ArrayList<>();

            for(int i = 0; i < f; i++){
                front.addAll(clean(tokens.get(i)));
            }

            String firstLabel = cleanLabel(tokens.get(f).get(tokens.get(f).size()-1));

            String firstName = "";

            for(String s: clean(tokens.get(f))){
                firstName += " "+s;
            }
            firstName = firstName.trim();

            List<String> inner = new ArrayList<>();

            for(int i = f+1; i < l; i++){
                inner.addAll(clean(tokens.get(i)));
            }

            String secondLabel = cleanLabel(tokens.get(l).get(tokens.get(l).size()-1));

            String secondName = "";

            for(String s: clean(tokens.get(l))){
                secondName += " "+s;
            }
            secondName = secondName.trim();

            List<String> back = new ArrayList<>();

            for(int i = l+1; i < tokens.size(); i++){
                back.addAll(clean(tokens.get(i)));
            }

            tagSink.push(new Pair<>(
                    new NEREntity(obj, firstName, firstLabel),
                    new NEREntity(obj, secondName, secondLabel)
                    ));

            sink.push(new SimpleLabelledEntity(firstLabel, secondLabel, "unknown",
                    ImmutableList.copyOf(front), ImmutableList.copyOf(inner), ImmutableList.copyOf(back)));

        }

        sink.stopSignal("NER");

    }

    private List<List<String>> tokenize(List<String> content, List<String> tags){
        List<List<String>> out = new ArrayList<>();
        List<String> tmp = new ArrayList<>();
        String actTag = "";

        for(int i = 0; i < content.size(); i++){
            if(actTag.equals(tags.get(i))){
                tmp.add(content.get(i).toLowerCase());
            }else {
                if(!tmp.isEmpty()) {
                    tmp.add(actTag);
                    out.add(tmp);
                }
                tmp = new ArrayList<>();
                tmp.add(content.get(i).toLowerCase());
                actTag = tags.get(i);
            }
        }

        if(!tmp.isEmpty()){
            tmp.add(actTag);
            out.add(tmp);
        }

        return out;
    }

    private List<Integer> index(List<List<String>> in){
        List<Integer> list = new ArrayList<>();

        for(int i = 0; i < in.size(); i++){
            List<String> indexable = in.get(i);
            if(!indexable.get(indexable.size()-1).equals("O")){
                list.add(i);
            }
        }

        return list;
    }

    private List<List<Integer>> constructIndexTuple(List<Integer> index){
        List<List<Integer>> out = new ArrayList<>();

        for(int i = 0; i < index.size(); i++){
            for(int j = i+1; j < index.size(); j++){
                List<Integer> list = new ArrayList<>();
                list.add(index.get(i));
                list.add(index.get(j));
                out.add(list);
            }
        }

        return out;
    }

    private List<String> clean(List<String> list){
        list = new ArrayList<>(list);
        list.remove(list.size()-1);
        return list;
    }

    private String cleanLabel(String s){
        if(s.equalsIgnoreCase("organization")){
            return "organisation";
        }
        return s;
    }
}
