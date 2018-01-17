import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import javafx.util.Pair;
import standfordner.IStandforNER;
import standfordner.StandforNER;

import java.io.IOException;
import java.util.List;


public class App {
    public static void main( String[] args ) throws IOException, ClassNotFoundException {
        String text = "Berlin is capital of Germany. Berlin was part of east Germany. " +
                "Barack obama who served as the 44th President of the United States, visited Germany "
                + "along Bill Gates founder of the Microsoft.";

        demoStandfordNerwithFilter (text);

    }

    public static void demoStandfordNerwithFilter (String text)throws IOException, ClassNotFoundException {
        String serializedClassifier = "classifier/english.all.3class.distsim.crf.ser.gz";
        IStandforNER standforNER = new StandforNER(serializedClassifier);

        List<Pair<String, String>> filterList = standforNER.filterClassify(text);

        for (Pair<String, String> pair: filterList){
            System.out.println('['+ pair.getKey()+','+pair.getValue()+']');
        }
    }

    public static void demoStandfordNer (String text)throws IOException, ClassNotFoundException {

        String serializedClassifier = "classifier/english.all.3class.distsim.crf.ser.gz";
        IStandforNER standforNER = new StandforNER(serializedClassifier);
        List<List<CoreLabel>> list = standforNER.classify(text);

        for (List<CoreLabel> sentence : list) {
            for (CoreLabel word : sentence) {
                System.out.print(word.word() + '/' + word.get(CoreAnnotations.AnswerAnnotation.class)+' ');
            }
            System.out.println();
        }
    }
}
