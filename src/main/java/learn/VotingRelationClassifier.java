package learn;

import com.google.common.collect.Multimap;
import model.ILexicalEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A relation classifier.
 *
 * Let X = (y_1, y_2, x_1, x_2, ..., x_n) be an feature vector with (y_1, y_2) are the entity labels.
 * It firstly selects the kNN(y_1,y_2) , a kNN model trained for the given label combination.
 * Then use the kNN to predict:
 *         x_1 --> m_1_1, m_1_2, m_1_3, ..., m_1_k
 *         x_2 --> m_2_1, m_2_2, m_2_3, ..., m_2_k
 *         ...
 *         x_n --> m_n_1, m_n_2, m_n_3, ..., m_n_k
 *
 * Where:
 *  m_i_j = (d_i_j, f_i_j, r_i_j) with f_i_j a feature, r_i_j a relation and d_i_j the calculated
 *  distant to x_i
 *
 * After this calculate for each possible relation r:
 *
 *         Sum_{i=1}^{n} Sum_{j=1}^{k} f(i, j, r)
 *       with:
 *             f(i, j, r) = (1 - d_i_j) * P(r_i_j | f_i_j) if r_i_j == r
 *             f(i, j, r) = 0 otherwise
 *
 * Take the relation r with the highest calculated value
 *
 * @author Cedric Richter
 */
public class VotingRelationClassifier implements IRelationClassifier {

    private RCModel model;

    public VotingRelationClassifier(RCModel model) {
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
        throw new UnsupportedOperationException();
    }
}
