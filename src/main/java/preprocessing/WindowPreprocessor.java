package preprocessing;

import com.google.common.collect.ImmutableList;
import model.ILabelledEntity;
import model.SimpleLabelledEntity;
import pipeline.APipe;

import java.util.ArrayList;
import java.util.List;

/**
 * A pipe which shrink the front and back window to a given number
 *
 * @author Cedric Richter
 */
public class WindowPreprocessor extends APipe<ILabelledEntity, ILabelledEntity> {

    private int k;

    public WindowPreprocessor(int k) {
        this.k = k;
    }

    @Override
    public void push(ILabelledEntity obj) {
        ImmutableList<String> front = shrinkOrExtend(obj.getFrontWindow(), k);
        ImmutableList<String> back = shrinkOrExtendBack(obj.getBackWindow(), k);

        sink.push(new SimpleLabelledEntity(obj.getFirstLabel(),
                obj.getSecondLabel(), obj.getPredicate(), front, obj.getInnerFeature(), back));
    }

    private ImmutableList<String> shrinkOrExtend(ImmutableList<String> list, int k){
        List<String> out = new ArrayList<>();
        if(list.size() < k){
            int d = k - list.size();

            for(int i = 0; i < d; i++)
                out.add("#");
        }

        int i = Math.max(0, list.size() - k);
        for(; i < list.size(); i++){
            out.add(list.get(i));
        }

        return ImmutableList.copyOf(out);
    }

    private ImmutableList<String> shrinkOrExtendBack(ImmutableList<String> list, int k){
        List<String> out = new ArrayList<>();

        int i = 0;
        for(; i < k && i < list.size(); i++){
            out.add(list.get(i));
        }

        if(out.size() < k){
            int d = k - list.size();

            for(int j = 0; j < d; j++)
                out.add("#");
        }

        return ImmutableList.copyOf(out);
    }
}
