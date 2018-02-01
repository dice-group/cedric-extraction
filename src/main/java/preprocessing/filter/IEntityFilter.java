package preprocessing.filter;

import model.ILabelledEntity;

/**
 * A filter which allow to mark labelled entities
 *
 * @author Cedric Richter
 */
public interface IEntityFilter {

    public boolean isFiltered(ILabelledEntity e);

}
