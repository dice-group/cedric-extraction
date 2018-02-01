package learn;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import model.ITrainingData;

import java.util.Map;
import java.util.Objects;

/**
 * A base model which manages different search spaces for different input categories,
 *
 * @author Cedric Richter
 */
public class RCModel {

    private Map<LabelKey, IRCSearchSpace> model;
    private IRCSearchSpaceFactory factory;

    public RCModel(Map<LabelKey, IRCSearchSpace> model, IRCSearchSpaceFactory factory) {
        this.model = model;
        this.factory = factory;
    }


    /**
     * Add the training data to the matching search space
     * @param data training data
     */
    public void addData(ITrainingData data){
        LabelKey key = new LabelKey(data.getCategory().getFirstLabel().toLowerCase(),
                                    data.getCategory().getSecondLabel().toLowerCase());

        if(!model.containsKey(key)){
            model.put(key, factory.create());
        }

        IRCSearchSpace space = model.get(key);

        space.extend(data);
    }

    Map<LabelKey, IRCSearchSpace> getModel(){
        return model;
    }


    /**
     * Uses the given label combination to select a search space.
     * Then the search spaces will be queried for the features
     * @param firstLabel first detected entity label
     * @param secondLabel second detected entity label
     * @param features a set of features for the given entity
     * @return a prediction of results for each feature (f --> 2^R)
     */
    public Multimap<String, IRCSearchResult> predict(String firstLabel, String secondLabel, Iterable<String> features){
        Multimap<String, IRCSearchResult> out = HashMultimap.create();

        LabelKey first = new LabelKey(firstLabel.toLowerCase(), secondLabel.toLowerCase());
        if(model.containsKey(first)){
            IRCSearchSpace space = model.get(first);

            out.putAll(space.search(features));
        }

        LabelKey second = new LabelKey(secondLabel.toLowerCase(), firstLabel.toLowerCase());
        if(!first.equals(second) && model.containsKey(second)){
            IRCSearchSpace space = model.get(second);

            out.putAll(space.search(features));
        }

        return out;
    }



    private class LabelKey{

        private String firstLabel, secondLabel;

        public LabelKey(String firstLabel, String secondLabel) {
            this.firstLabel = firstLabel;
            this.secondLabel = secondLabel;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LabelKey)) return false;
            LabelKey labelKey = (LabelKey) o;
            return Objects.equals(firstLabel, labelKey.firstLabel) &&
                    Objects.equals(secondLabel, labelKey.secondLabel);
        }

        @Override
        public int hashCode() {

            return Objects.hash(firstLabel, secondLabel);
        }


    }
}
