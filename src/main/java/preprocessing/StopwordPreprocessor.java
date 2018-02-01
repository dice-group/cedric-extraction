package preprocessing;

import com.google.common.collect.ImmutableList;
import model.ILabelledEntity;
import model.SimpleLabelledEntity;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.util.Version;
import pipeline.APipe;

import java.util.ArrayList;
import java.util.List;

/**
 * A pipe which identify stop words in the feature set and remove them
 *
 * @author Cedric Richter
 */
public class StopwordPreprocessor extends APipe<ILabelledEntity, ILabelledEntity> {

    private CharArraySet stopwords;

    public StopwordPreprocessor(CharArraySet set){
        this.stopwords = CharArraySet.copy(Version.LUCENE_36, set);
        this.stopwords.add("-lrb-");
        this.stopwords.add("-rrb-");
        this.stopwords.add(".");
        this.stopwords.add("``");
        this.stopwords.add("\'\'");
        this.stopwords.add("--");
        this.stopwords.add("\'s");
    }

    public StopwordPreprocessor(){
        this((CharArraySet) StopAnalyzer.ENGLISH_STOP_WORDS_SET);
    }

    @Override
    public void push(ILabelledEntity obj) {

        sink.push(new SimpleLabelledEntity(
                obj.getFirstLabel(),
                obj.getSecondLabel(),
                obj.getPredicate(),
                extractStopwords(obj.getFrontWindow()),
                extractStopwords(obj.getInnerFeature()),
                extractStopwords(obj.getBackWindow())
        ));

    }

    private ImmutableList<String> extractStopwords(ImmutableList<String> list){
        List<String> out = new ArrayList<>();

        for(String s: list){
            if(!stopwords.contains(s.toLowerCase()))
                out.add(s);
        }

        return ImmutableList.copyOf(out);
    }
}
