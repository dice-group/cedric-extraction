package model;

import bayes.ICategory;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Objects;

public class SimpleTrainingData implements ITrainingData {

    private ICategory category;
    private ImmutableSet<ILexicalFeature> features;

    public SimpleTrainingData(ICategory category, ImmutableSet<ILexicalFeature> features) {
        this.category = category;
        this.features = features;
    }


    @Override
    public ICategory getCategory() {
        return category;
    }

    @Override
    public ImmutableSet<ILexicalFeature> getFeatures() {
        return features;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ITrainingData)) return false;
        ITrainingData that = (ITrainingData) o;
        return Objects.equals(getCategory(), that.getCategory()) &&
                Objects.equals(getFeatures(), that.getFeatures());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getCategory(), getFeatures());
    }
}
