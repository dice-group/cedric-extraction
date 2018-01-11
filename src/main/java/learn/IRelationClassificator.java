package learn;

import model.ILexicalEntity;

import java.io.IOException;

public interface IRelationClassificator {

    public String predict(ILexicalEntity entity);

    public void save(String path) throws IOException;

}
