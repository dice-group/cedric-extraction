package pipeline;

import com.sun.tools.javac.util.Pair;

/**
 * An abstract class which helps to implement a pipe which splits the input
 * @author Cedric Richter
 * @param <S> source type
 * @param <A> first sink type
 * @param <T> second sink type
 */
public abstract class SplitPipe<S, A, T> implements ISink<S>{

    private ISink<A> sinkA;
    private ISink<T> sinkT;

    public void setFirstSink(ISink<A> sink){
        this.sinkA = sink;
    }

    public void setSecondSink(ISink<T> sink){
        this.sinkT = sink;
    }

    @Override
    public void push(S obj) {
        Pair<A, T> pair = split(obj);
        sinkA.push(pair.fst);
        sinkT.push(pair.snd);
    }

    @Override
    public void stopSignal(String type) {
        sinkA.stopSignal("A:"+type);
        sinkT.stopSignal("T:"+type);
    }

    public abstract Pair<A, T> split(S in);
}
