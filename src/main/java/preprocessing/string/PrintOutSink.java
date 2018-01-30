package preprocessing.string;

import javafx.util.Pair;
import learn.validate.IClassificationTestResult;
import model.Relation;
import model.RelationEntity;
import pipeline.ISink;

public class PrintOutSink implements ISink<Relation> {
    @Override
    public void push(Relation obj) {

        String first = obj.getSubject().getUri();
        String second = obj.getObject().getUri();
        String predicate = obj.getRelation();

        if(!predicate.equalsIgnoreCase("unknown"))
            System.out.println("<"+first+ "> <"+predicate+ "> <"+second+">");

    }

    @Override
    public void stopSignal(String type) {

    }
}
