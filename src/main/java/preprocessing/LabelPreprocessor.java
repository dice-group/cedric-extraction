package preprocessing;


import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import model.ILabelledEntity;
import model.SimpleLabelledEntity;
import pipeline.APipe;

import java.util.*;

/**
 * Sometimes different labels are used with the same meaning.
 * Like place and location are meant to be the same entity label.
 *
 * This processor unify this labels to one common label.
 *
 * @author Cedric Richter
 */
public class LabelPreprocessor extends APipe<ILabelledEntity, ILabelledEntity> {


    public static Multimap<String, String> getDefaultSameLabel(){
        Multimap<String, String> same = HashMultimap.create();
        same.put("location", "place");
        same.put("place", "location");

        return same;
    }

    private Multimap<String, String> sameLabel;

    /**
     * The given map have to map each same relation in both direct.
     * Like:
     *  Location and Place should be mapped by:
     *      Location --> Place
     *      Place --> Location
     * @param sameLabel a multimap of labels which are defined to be the same
     */
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
