package preprocessing.string;

import javafx.util.Pair;
import learn.validate.IClassificationTestResult;
import pipeline.ISink;

public class PrintOutSink implements ISink<Pair<Pair<String, String>, IClassificationTestResult>> {
    @Override
    public void push(Pair<Pair<String, String>, IClassificationTestResult> obj) {

        String first = obj.getKey().getKey();
        String second = obj.getKey().getValue();
        String predicate = obj.getValue().getPredictedRelation();

        if(!predicate.equalsIgnoreCase("unknown"))
            System.out.println("<"+first+ "> <"+predicate+ "> <"+second+">");

    }

    @Override
    public void stopSignal(String type) {

    }
}
