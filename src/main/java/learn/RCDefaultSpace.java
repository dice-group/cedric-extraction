package learn;

import bayes.IBayesEstimator;
import bayes.ICategory;
import bayes.UnknownCategoryException;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import model.ILexicalFeature;
import model.ITrainingData;
import neighbours.INamedObject;
import neighbours.JaccardNgramMeasure;
import neighbours.NeighbourSearch;

import java.util.*;

/**
 * A default implementation for the RCSearchSpace
 *
 * It is based on k Nearest Neighbours as search approach
 *
 * @author Cedric Richter
 */
public class RCDefaultSpace implements IRCSearchSpace{

    private int k;

    private IBayesEstimator estimator;
    private NeighbourSearch search = new NeighbourSearch(new JaccardNgramMeasure(3));

    public RCDefaultSpace(int k, IBayesEstimator estimator) {
        this.k = k;
        this.estimator = estimator;
    }

    @Override
    public Multimap<String, IRCSearchResult> search(Iterable<String> features) {
        Multimap<String, IRCSearchResult> out = HashMultimap.create();

        for(String s: features){
            List<NeighbourSearch.DistantObject> l =  search.getNNDistant(s, k);

            for(NeighbourSearch.DistantObject o: l)
                out.put(s, new DistantScaledResult(o.getDistant(), (IRCSearchResult)o.getContent()));
        }

        return out;
    }

    @Override
    public void extend(ITrainingData data) {

        for(ILexicalFeature feature: data.getFeatures()){
            search.add(new RCDefaultResult(feature,
                    data.getCategory()
            ));

        }

    }

    /**
     *
     * @return the set of saved results
     */
    public Set<IRCSearchResult> getPossibleResults(){
        Set<IRCSearchResult> out = new HashSet<>();

        for(INamedObject obj: search)
            out.add((IRCSearchResult)obj);

        return out;
    }

    private class RCDefaultResult implements INamedObject, IRCSearchResult{

        private ILexicalFeature feature;

        private boolean accDef = false;
        private double accuracy;

        private ICategory category;

        public RCDefaultResult(ILexicalFeature feature, ICategory category) {
            this.feature = feature;
            this.category = category;
        }


        @Override
        public ILexicalFeature getFeature() {
            return feature;
        }

        @Override
        public double getAccuracy() {
            if(!accDef){
                List<String> est = Arrays.asList(
                        new String[]{feature.getFeature()}
                );

                try {
                    accuracy = estimator.estimate(est, category).get(feature.getFeature());
                } catch (UnknownCategoryException e) {
                }
            }
            return accuracy;
        }

        @Override
        public String getClassification() {
            return category.getPredicate();
        }

        @Override
        public String getName() {
            return feature.getFeature();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IRCSearchResult)) return false;
            IRCSearchResult that = (IRCSearchResult) o;
            return Objects.equals(getFeature(), that.getFeature()) &&
                    Objects.equals(category.getPredicate(), that.getClassification());
        }

        @Override
        public int hashCode() {

            return Objects.hash(getFeature(), category.getPredicate());
        }

    }
}
