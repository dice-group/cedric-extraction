package learn;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import model.ITrainingData;

import java.util.Map;
import java.util.Objects;

public class RCModel {

    private Map<LabelKey, IRCSearchSpace> model;
    private IRCSearchSpaceFactory factory;

    public RCModel(Map<LabelKey, IRCSearchSpace> model, IRCSearchSpaceFactory factory) {
        this.model = model;
        this.factory = factory;
    }


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
