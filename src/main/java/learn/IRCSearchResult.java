package learn;

import model.ILexicalFeature;

public interface IRCSearchResult {

    public ILexicalFeature getFeature();

    public double getAccuracy();

    public String getClassification();

}
