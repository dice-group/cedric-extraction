package model;

import bayes.ICategory;

import java.util.Objects;

public class SimpleCategory implements ICategory {

    private String firstLabel, secondLabel, predicate;

    public SimpleCategory(String firstLabel, String secondLabel, String predicate) {
        this.firstLabel = firstLabel;
        this.secondLabel = secondLabel;
        this.predicate = predicate;
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
    public String getPredicate() {
        return predicate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ICategory)) return false;
        ICategory that = (ICategory) o;
        return Objects.equals(getFirstLabel(), that.getFirstLabel()) &&
                Objects.equals(getSecondLabel(), that.getSecondLabel()) &&
                Objects.equals(getPredicate(), that.getPredicate());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getFirstLabel(), getSecondLabel(), getPredicate());
    }


}
