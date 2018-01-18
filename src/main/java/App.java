import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import javafx.util.Pair;
import standfordner.IStandforNER;
import standfordner.StandforNER;

import java.io.IOException;
import java.util.List;


public class App {
    public static void main( String[] args ) throws IOException, ClassNotFoundException {
        String text = "Berlin is the capital of Germany. " +
                "Barack Obama who served as the 44th President of the United States, visited Germany. "
                + "Bill Gates is the founder of the Microsoft. Bill Gates has visited the Researchgate team in Berlin";

        demoStandfordNERWithFilter (text);

    }

    public static void demoStandfordNERWithFilter (String text)throws IOException, ClassNotFoundException {
        String serializedClassifier = "classifier/english.all.3class.distsim.crf.ser.gz";
        IStandforNER standforNER = new StandforNER(serializedClassifier);

        List<Pair<String, String>> filterList = standforNER.filterClassify(text);

        for (Pair<String, String> pair: filterList){
            System.out.println('['+ pair.getKey()+','+pair.getValue()+']');
        }
    }

    public static void demoStandfordNER (String text)throws IOException, ClassNotFoundException {

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
