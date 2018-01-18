package learn;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Multimap;
import model.ILexicalEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

public class BayesRelationClassificator implements IRelationClassificator {

    private RCModel model;

    public BayesRelationClassificator(RCModel model) {
        this.model = model;
    }

    @Override
    public String predict(ILexicalEntity entity) {

        List<String> features = new ArrayList<>(entity.getFrontWindow());
        features.addAll(entity.getInnerFeature());
        features.addAll(entity.getBackWindow());


        Multimap<String, IRCSearchResult> results =
                model.predict(entity.getFirstLabel(),
                                entity.getSecondLabel(),
                                features);

        BigDecimal initPlace = new BigDecimal(1);
        Map<String, BigDecimal> regressionMap = new HashMap<>();

        //Predict prob for classification
        for(String f: features){
            Collection<IRCSearchResult> result = results.get(f);

            Set<String> unseen = new HashSet<>(regressionMap.keySet());

            //Sum voting
            Map<String, BigDecimal> sum = new HashMap<>();

            for(IRCSearchResult r: result){
                unseen.remove(r.getClassification());
                if(!sum.containsKey(r.getClassification())){
                    sum.put(r.getClassification(), BigDecimal.ZERO);
                }

                BigDecimal act = sum.get(r.getClassification());
                BigDecimal add = new BigDecimal(r.getAccuracy());
                add = add.divide(new BigDecimal(result.size()), MathContext.DECIMAL128);
                act = act.add(add, MathContext.DECIMAL128);

                sum.put(r.getClassification(), act);
            }

            if(sum.isEmpty())
                continue;

            BigDecimal half = new BigDecimal(0.0);
            for(String s: unseen)
                sum.put(s, half);

            //Calculate pred
            for(Map.Entry<String, BigDecimal> e: sum.entrySet()){
                if(!regressionMap.containsKey(e.getKey()))
                    regressionMap.put(e.getKey(), initPlace);

                regressionMap.put(e.getKey(),
                        regressionMap.get(e.getKey()).multiply(e.getValue(), MathContext.DECIMAL128));
            }

            initPlace = initPlace.divide(new BigDecimal(2), MathContext.DECIMAL128);
        }

        String classification = "unknown";
        BigDecimal max = new BigDecimal(0);

        for(Map.Entry<String, BigDecimal> e: regressionMap.entrySet()) {
            if(e.getValue().compareTo(max)>0){
                classification = e.getKey();
                max = e.getValue();
            }
        }

        return classification;
    }


    public Multimap<String, String> getTypicalWordsPerRelation(int k){

        Map<String, MinMaxPriorityQueue<IRCSearchResult>> queues = new HashMap<>();
        SearchResultComparator comp = new SearchResultComparator();

        for(IRCSearchSpace space: model.getModel().values()){
            if(space instanceof RCDefaultSpace){
                Set<IRCSearchResult> results = ((RCDefaultSpace) space).getPossibleResults();
                for(IRCSearchResult result: results){
                    String pred = result.getClassification();

                    if(!queues.containsKey(pred)){
                        queues.put(pred,MinMaxPriorityQueue.orderedBy(comp).maximumSize(k).create());
                    }

                    queues.get(pred).add(result);
                }
            }
        }

        Multimap<String, String> out = HashMultimap.create();

        for(Map.Entry<String, MinMaxPriorityQueue<IRCSearchResult>> e: queues.entrySet()){
            for(IRCSearchResult result: e.getValue()){
                out.put(e.getKey(), result.getFeature().getFeature());
            }
        }

        return out;
    }

    private class SearchResultComparator implements Comparator<IRCSearchResult>{

        @Override
        public int compare(IRCSearchResult o1, IRCSearchResult o2) {
            return (int) Math.signum(o2.getAccuracy()-o1.getAccuracy());
        }
    }

    @Override
    public void save(String path) throws IOException {

    }
}
