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

import java.util.Arrays;
import java.util.List;

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
            List<INamedObject> l =  search.getNearestNeighbour(s, k);

            for(INamedObject o: l)
                out.put(s, (IRCSearchResult)o);
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
    }
}
