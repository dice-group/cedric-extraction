package model;

import bayes.ICategory;
import com.google.common.collect.ImmutableSet;

public interface ITrainingData {

    public ICategory getCategory();

    public ImmutableSet<ILexicalFeature> getFeatures();

}
