package bayes;

import model.ILabelledEntity;
import model.ILexicalEntity;

import java.util.Map;

public interface IBayesEstimator {

    public void train(ILabelledEntity entity);

    public Map<String, Double> estimate(Iterable<String> data, ICategory cat) throws UnknownCategoryException;

}
