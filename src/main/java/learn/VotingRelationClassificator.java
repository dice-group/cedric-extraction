package learn;

import com.google.common.collect.Multimap;
import model.ILexicalEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VotingRelationClassificator implements IRelationClassificator{

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

        Map<String, BigDecimal> votingMap = new HashMap<>();

        for(IRCSearchResult result: results.values()){
            String clazz = result.getClassification();

            if(!votingMap.containsKey(clazz)){
                votingMap.put(clazz, BigDecimal.ZERO);
            }

            votingMap.put(clazz, votingMap.get(clazz).add(BigDecimal.valueOf(result.getAccuracy())));
        }

        String classification = "unknown";
        BigDecimal max = new BigDecimal(Integer.MIN_VALUE);

        for(Map.Entry<String, BigDecimal> e: votingMap.entrySet()) {
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
