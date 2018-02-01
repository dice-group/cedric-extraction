package preprocessing.filter;

import model.ILabelledEntity;

/**
 * A filter based on the label of the labelled entity
 *
 * @author Cedric Richter
 */
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
