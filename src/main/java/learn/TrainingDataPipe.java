package learn;

import com.google.common.collect.ImmutableSet;
import model.*;
import pipeline.APipe;

import java.util.ArrayList;
import java.util.List;

/**
 * A pipe which transforms labelled entities to training data for the relation classifier
 *
 * @author Cedric Richter
 */
public class TrainingDataPipe extends APipe<ILabelledEntity, ITrainingData> {
    @Override
    public void push(ILabelledEntity obj) {
        List<String> terms = new ArrayList<>(obj.getFrontWindow());
        terms.addAll(obj.getInnerFeature());
        terms.addAll(obj.getBackWindow());

        List<ILexicalFeature> features = new ArrayList<>();

        for(String s: terms){
            if(!ignoreTerm(s))features.add(new SimpleLexicalFeature(s));
        }

        sink.push(new SimpleTrainingData(
                new SimpleCategory(obj.getFirstLabel(), obj.getSecondLabel(), obj.getPredicate()),
                ImmutableSet.copyOf(features)
        ));

    }

    protected boolean ignoreTerm(String term){
        return term.equals("#");
    }



}
