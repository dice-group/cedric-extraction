package neighbours;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

/**
 * A distant measure which splits the string in ngrams and then uses jaccard distance
 *
 * @author Cedric Richter
 */
public class JaccardNgramMeasure implements IStringMeasure {

    private int n;

    public JaccardNgramMeasure(int n) {
        this.n = n;
    }


    @Override
    public boolean inFilter(String s1, String s2, double minDist) {
        double acceptance = 1 - minDist;
        Set<String> n1 = ngrams(s1);
        Set<String> n2 = ngrams(s2);

        int size1 = n1.size();
        int size2 = n2.size();

        //Filter 1
        double up = ((double)size1)/acceptance;
        double down = ((double)size1)*acceptance;

        if(down > size2 || size2 > up)
            return false;

        //Filter 2
        n1.retainAll(n2);

        if(n1.isEmpty())
            return false;

        //Filter 3
        return acceptance*Math.max(size1, size2) <= n1.size();
    }

    @Override
    public double getDistance(String s1, String s2) {
        Set<String> n1 = ngrams(s1);
        Set<String> n2 = ngrams(s2);

        if(n1.size() > n2.size()){
            Set<String> tmp = n1;
            n1 = n2;
            n2 = tmp;
        }

        Set<String> union = new HashSet<>(n1);
        union.addAll(n2);

        n1.retainAll(n2);

        return 1 - ((double)n1.size()/union.size());
    }

    public Set<String> ngrams(String in){
        Set<String> ngrams = new HashSet<>();

        if(in.length() < n){
            do{
                in += "#";
            }while(in.length() < n);
            ngrams.add(in);
            return ngrams;
        }

        for(int i = 0; i < in.length()-n+1; i++){
            ngrams.add(in.substring(i, i+n));
        }

        return ngrams;
    }
}
