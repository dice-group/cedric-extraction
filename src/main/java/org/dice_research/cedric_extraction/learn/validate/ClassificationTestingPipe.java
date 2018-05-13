package org.dice_research.cedric_extraction.learn.validate;

import org.dice_research.cedric_extraction.learn.IRelationClassifier;
import org.dice_research.cedric_extraction.learn.validate.simple.SimpleClassificationTestResult;
import org.dice_research.cedric_extraction.model.ILabelledEntity;
import org.dice_research.cedric_extraction.pipeline.APipe;

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
