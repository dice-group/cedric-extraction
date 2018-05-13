package org.dice_research.cedric_extraction.preprocessing;

import org.dice_research.cedric_extraction.io.IEntityMapper;
import org.dice_research.cedric_extraction.model.ILabelledEntity;
import org.dice_research.cedric_extraction.pipeline.APipe;
import org.dice_research.cedric_extraction.preprocessing.filter.IEntityFilter;

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
