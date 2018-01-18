package standfordner;

import edu.stanford.nlp.ling.CoreLabel;
import javafx.util.Pair;

import java.util.List;

public interface IStandforNER {

    List<List<CoreLabel>> classify (String text);

    List<Pair<String, String>> filterClassify (String text);
}
