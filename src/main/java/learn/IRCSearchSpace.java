package learn;

import com.google.common.collect.Multimap;
import model.ITrainingData;


public interface IRCSearchSpace {

    public Multimap<String, IRCSearchResult> search(Iterable<String> features);

    public void extend(ITrainingData data);

}
