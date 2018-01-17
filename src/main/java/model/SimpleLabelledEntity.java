package model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class SimpleLabelledEntity implements ILabelledEntity{

    private String firstLabel;
    private String secondLabel;
    private String predicate;
    private ImmutableList<String> frontWindow, innerFeature, backWindow;

    public SimpleLabelledEntity(String firstLabel, String secondLabel, String predicate,
                                ImmutableList<String> frontWindow, ImmutableList<String> innerFeature,
                                ImmutableList<String> backWindow) {
        this.firstLabel = firstLabel;
        this.secondLabel = secondLabel;
        this.predicate = predicate;
        this.frontWindow = frontWindow;
        this.innerFeature = innerFeature;
        this.backWindow = backWindow;
    }

    @Override
    public String getPredicate() {
        return predicate;
    }

    @Override
    public String getFirstLabel() {
        return firstLabel;
    }

    @Override
    public String getSecondLabel() {
        return secondLabel;
    }

    @Override
    public ImmutableList<String> getFrontWindow() {
        return frontWindow;
    }

    @Override
    public ImmutableList<String> getBackWindow() {
        return backWindow;
    }

    @Override
    public ImmutableList<String> getInnerFeature() {
        return innerFeature;
    }
}
