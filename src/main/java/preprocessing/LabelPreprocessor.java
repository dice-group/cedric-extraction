package preprocessing;


import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import model.ILabelledEntity;
import model.SimpleLabelledEntity;
import pipeline.APipe;

import java.util.*;

public class LabelPreprocessor extends APipe<ILabelledEntity, ILabelledEntity> {


    public static Multimap<String, String> getDefaultSameLabel(){
        Multimap<String, String> same = HashMultimap.create();
        same.put("location", "place");
        same.put("place", "location");

        return same;
    }

    private Multimap<String, String> sameLabel;

    public LabelPreprocessor(Multimap<String, String> sameLabel) {
        this.sameLabel = sameLabel;
    }

    public LabelPreprocessor(){
        this(getDefaultSameLabel());
    }

    @Override
    public void push(ILabelledEntity obj) {

        sink.push(new SimpleLabelledEntity(
                preprocess(obj.getFirstLabel()),
                preprocess(obj.getSecondLabel()),
                obj.getPredicate(),
                obj.getFrontWindow(),
                obj.getInnerFeature(),
                obj.getBackWindow()
        ));

    }

    private String preprocess(String label){
        label = label.toLowerCase().trim();

        List<String> same = new ArrayList<>(sameLabel.get(label));
        same.add(label);

        Collections.sort(same);

        return same.get(0);
    }


}
