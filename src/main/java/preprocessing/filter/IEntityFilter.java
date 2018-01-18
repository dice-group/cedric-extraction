package preprocessing.filter;

import model.ILabelledEntity;

public interface IEntityFilter {

    public boolean isFiltered(ILabelledEntity e);

}
