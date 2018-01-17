package model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SimpleLexicalFeature implements ILexicalFeature{

    private String feature;
    private Map<String, Object> properties;

    public SimpleLexicalFeature(String feature) {
        this.feature = feature;
    }


    @Override
    public String getFeature() {
        return feature;
    }

    @Override
    public Map<String, Object> getProperties() {
        if(properties == null){
            properties = new HashMap<>();
        }
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ILexicalFeature)) return false;
        ILexicalFeature that = (ILexicalFeature) o;
        return Objects.equals(getFeature(), that.getFeature());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getFeature());
    }
}
