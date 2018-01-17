package preprocessing;

import com.google.common.collect.ImmutableList;
import model.ILabelledEntity;
import model.SimpleLabelledEntity;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopAnalyzer;
import pipeline.APipe;

import java.util.ArrayList;
import java.util.List;

public class StopwordPreprocessor extends APipe<ILabelledEntity, ILabelledEntity> {

    private CharArraySet stopwords;

    public StopwordPreprocessor(CharArraySet set){
        this.stopwords = set;
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
