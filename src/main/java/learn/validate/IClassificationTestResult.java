package learn.validate;

import model.ILexicalEntity;

public interface IClassificationTestResult {

    public ILexicalEntity getEntity();

    public String getPredictedRelation();

    public String getValidRelation();

}
