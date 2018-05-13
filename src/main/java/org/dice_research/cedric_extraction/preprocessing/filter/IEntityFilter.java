package org.dice_research.cedric_extraction.preprocessing.filter;

import org.dice_research.cedric_extraction.model.ILabelledEntity;

/**
 * A filter which allow to mark labelled entities
 *
 * @author Cedric Richter
 */
public interface IEntityFilter {

    public boolean isFiltered(ILabelledEntity e);

}
