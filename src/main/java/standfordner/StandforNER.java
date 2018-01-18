package standfordner;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StandforNER implements IStandforNER {

    private AbstractSequenceClassifier<CoreLabel> classifier;

    public StandforNER (String serializedClassifier) throws IOException, ClassNotFoundException {
        classifier =  CRFClassifier.getClassifier(serializedClassifier);
    }

    @Override
    public List<List<CoreLabel>> classify(String text) {
        return  classifier.classify(text);
    }

    @Override
    public List<Pair<String, String>> filterClassify(String text) {
        List<List<CoreLabel>> out = classifier.classify(text);
        List<Pair<String, String>> filterList = new ArrayList<Pair<String, String>>();

        for (List<CoreLabel> sentence : out) {
            for (CoreLabel word : sentence) {
                String annotation = word.get(CoreAnnotations.AnswerAnnotation.class);
                if (!annotation.equals("O")) {
                    filterList.add(new Pair<String, String>(word.word(), annotation));
                }
            }
        }
        return filterList;
    }
}
