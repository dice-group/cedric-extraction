package learn.validate.simple;

import learn.validate.IClassificationTestResult;
import model.ILabelledEntity;
import model.ILexicalEntity;

public class SimpleClassificationTestResult implements IClassificationTestResult {

    private ILabelledEntity entity;
    private String prediction;


    public SimpleClassificationTestResult(ILabelledEntity entity, String prediction){
        this.entity = entity;
        this.prediction = prediction;
    }

    @Override
    public ILexicalEntity getEntity() {
        return entity;
    }

    @Override
    public String getPredictedRelation() {
        return prediction;
    }

    @Override
    public String getValidRelation() {
        return entity.getPredicate();
    }
}
