package learn.validate;

import learn.IRelationClassificator;
import model.ILabelledEntity;
import pipeline.APipe;

public class ClassificationTestingPipe extends APipe<ILabelledEntity, IClassificationTestResult> {

    private IRelationClassificator classificator;

    public ClassificationTestingPipe(IRelationClassificator classificator) {
        this.classificator = classificator;
    }

    @Override
    public void push(ILabelledEntity obj) {

        String pred = classificator.predict(obj);

        sink.push(new SimpleClassificationTestResult(obj, pred));

    }
}
