package bayes;

import model.ILabelledEntity;
import model.ILexicalEntity;

import java.util.*;

/**
 *
 * An implementation for Bayes estimator.
 * It estimates probabilities based on the term frequency inverse document frequency count
 * (tfidf).
 *
 * The Tfidf approach gives features a high probability which are typically used in the
 * context of the given category. Features which are often used with different categories
 * got a low probability.
 *
 * @author Cedric Richter
 */
public class TfidfBayesEstimator implements IBayesEstimator {

    private double alpha;

    private long documentsCount;

    private Map<String, Integer> shareDocs = new HashMap<>();
    private Map<ICategory, TermStats> stats = new HashMap<>();

    /**
     *
     * @param alpha the bayes smoothing for unknown features
     */
    public TfidfBayesEstimator(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public void train(ILabelledEntity entity) {
        Category cat = new Category(entity.getFirstLabel(), entity.getSecondLabel(), entity.getPredicate());

        List<String> terms = new ArrayList<>(entity.getFrontWindow());
        terms.addAll(entity.getInnerFeature());
        terms.addAll(entity.getBackWindow());

        TermStats stat = getOrCreate(cat);

        for(String t: terms) {
            incrDocStats(t);
            stat.incrFreq(t);
            stat.termCount++;
        }

        documentsCount++;
    }

    private double estimate(ICategory cat, String s) throws UnknownCategoryException {
        if(!stats.containsKey(cat))
            throw new UnknownCategoryException();

        TermStats stat = stats.get(cat);

        double idf = 0.0;
        if(shareDocs.containsKey(s))
            idf = Math.log((double)documentsCount / shareDocs.get(s));

        double out = stat.get(s)*idf + alpha;

        out = out/(stat.termCount + alpha * shareDocs.size());

        return out;
    }

    @Override
    public Map<String, Double> estimate(Iterable<String> data, ICategory cat) throws UnknownCategoryException {
        Map<String, Double> map = new HashMap<>();

        for(String s: data){
            if(!map.containsKey(s))
                map.put(s, estimate(cat, s));
        }

        return map;
    }


    private void incrDocStats(String s){
        if(!shareDocs.containsKey(s))
            shareDocs.put(s, 0);

        shareDocs.put(s, shareDocs.get(s)+1);
    }

    private TermStats getOrCreate(ICategory cat){
        if(!stats.containsKey(cat))
            stats.put(cat, new TermStats());

        return stats.get(cat);
    }

    private class Category implements ICategory{

        private String first;
        private String second;
        private String predicate;

        public Category(String first, String second, String predicate) {
            this.first = first;
            this.second = second;
            this.predicate = predicate;
        }


        @Override
        public String getFirstLabel() {
            return first;
        }

        @Override
        public String getSecondLabel() {
            return second;
        }

        @Override
        public String getPredicate() {
            return predicate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ICategory)) return false;
            ICategory category = (ICategory) o;
            return Objects.equals(getFirstLabel(), category.getFirstLabel()) &&
                    Objects.equals(getSecondLabel(), category.getSecondLabel()) &&
                    Objects.equals(getPredicate(), category.getPredicate());
        }

        @Override
        public int hashCode() {

            return Objects.hash(getFirstLabel(), getSecondLabel(), getPredicate());
        }

    }


    private class TermStats{
        private long termCount = 0;
        private Map<String, Integer> termFrequency = new HashMap<>();

        public void incrFreq(String s){
            termFrequency.put(s, get(s)+1);
        }

        public int get(String s){
            if(termFrequency.containsKey(s))
                return termFrequency.get(s);
            return 0;
        }
    }
}
