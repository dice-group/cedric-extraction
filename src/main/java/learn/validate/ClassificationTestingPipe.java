package learn.validate;

import learn.IRelationClassifier;
import learn.validate.simple.SimpleClassificationTestResult;
import model.ILabelledEntity;
import pipeline.APipe;

/**
 *
 * A pipe which predicts a relation for a given entity and emits the prediction.
 *
 * @author Cedric Richter
 */
public class ClassificationTestingPipe extends APipe<ILabelledEntity, IClassificationTestResult> {

    private IRelationClassifier classificator;

    /**
     *
     * @param classificator the classifier used for predictions
     */
    public ClassificationTestingPipe(IRelationClassifier classificator) {
        this.classificator = classificator;
    }

    @Override
    public void push(ILabelledEntity obj) {

        String pred = classificator.predict(obj);

        sink.push(new SimpleClassificationTestResult(obj, pred));

    }
}
