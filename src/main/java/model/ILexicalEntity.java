package model;

import com.google.common.collect.ImmutableList;

public interface ILexicalEntity {

    public String getFirstLabel();

    public String getSecondLabel();

    public ImmutableList<String> getFrontWindow();

    public ImmutableList<String> getBackWindow();

    public ImmutableList<String> getInnerFeature();

}
