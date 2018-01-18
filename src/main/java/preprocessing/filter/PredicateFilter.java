package preprocessing.filter;

import model.ILabelledEntity;

public class PredicateFilter implements IEntityFilter {

    private String predicate;

    public PredicateFilter(String predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean isFiltered(ILabelledEntity e) {
        return predicate.equals(e.getPredicate());
    }
}
