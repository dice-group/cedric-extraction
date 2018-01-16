package learn;

import com.google.common.collect.Multimap;
import model.ILexicalEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

public class VotingRelationClassificator implements IRelationClassificator {

    private RCModel model;

    public VotingRelationClassificator(RCModel model) {
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
                act = act.add(new BigDecimal(r.getAccuracy()), MathContext.DECIMAL128);

                sum.put(r.getClassification(), act);
            }

            BigDecimal half = new BigDecimal(0.5);
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

        String classification = "";
        BigDecimal max = new BigDecimal(0);

        for(Map.Entry<String, BigDecimal> e: regressionMap.entrySet()) {
            if(e.getValue().compareTo(max)>0){
                classification = e.getKey();
                max = e.getValue();
            }
        }

        return classification;
    }

    @Override
    public void save(String path) throws IOException {

    }
}
