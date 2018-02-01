package preprocessing;

import io.IEntityMapper;
import model.ILabelledEntity;
import pipeline.APipe;
import preprocessing.filter.IEntityFilter;

import java.util.List;

/**
 * A pipe which only emits entities which are not marked by any defined filter
 *
 * @author Cedric Richter
 */
public class FilterPreprocessor extends APipe<ILabelledEntity, ILabelledEntity> {

    private List<IEntityFilter> filters;

    public FilterPreprocessor(List<IEntityFilter> filters) {
        this.filters = filters;
    }

    @Override
    public void push(ILabelledEntity obj) {
        boolean filter = false;

        for(IEntityFilter f: filters){
            filter |= f.isFiltered(obj);

            if(filter)
                break;
        }

        if(!filter)
            sink.push(obj);

    }
}
