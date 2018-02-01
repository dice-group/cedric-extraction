package preprocessing.string;

import model.Relation;
import pipeline.ISink;

import java.io.PrintStream;
import java.io.Writer;

/**
 * A sink which writes relations in Turtle format to a given output stream
 *
 * @author Cedric Richter
 */
public class WriteOutSink implements ISink<Relation> {

    private PrintStream writer;

    public WriteOutSink(PrintStream writer) {
        this.writer = writer;
    }

    @Override
    public void push(Relation obj) {

        String first = obj.getSubject().getUri();
        String second = obj.getObject().getUri();
        String predicate = obj.getRelation();

        if(!predicate.equalsIgnoreCase("unknown"))
            writer.println("<"+first+ "> <"+predicate+ "> <"+second+">.");

    }

    @Override
    public void stopSignal(String type) {

    }
}
