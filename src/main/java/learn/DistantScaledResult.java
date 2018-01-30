package learn;

import model.ILexicalFeature;
import neighbours.NeighbourSearch;

public class DistantScaledResult implements IRCSearchResult {

    private double distant;
    private IRCSearchResult result;

    public DistantScaledResult(double distant, IRCSearchResult result) {
        this.distant = distant;
        this.result = result;
    }

    @Override
    public ILexicalFeature getFeature() {
        return result.getFeature();
    }

    @Override
    public double getAccuracy() {
        return (1-distant)*result.getAccuracy();
    }

    @Override
    public String getClassification() {
        return result.getClassification();
    }
}
