package pipeline;



import javafx.util.Pair;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A pipe which synchronizes two sources and outputs a pair of results.
 * @author Cedric Richter
 * @param <S> first source type
 * @param <T> second source type
 */
public class MergePipe<S, T>{

    private ISink<Pair<S, T>> sink;

    private Queue<S> queueS = new LinkedBlockingQueue<>();
    private Queue<T> queueT = new LinkedBlockingQueue<>();

    public void setSink(ISink<Pair<S, T>> sink){
        this.sink = sink;
    }

    private void sync(){
        if(!queueS.isEmpty() && !queueT.isEmpty()){
            sink.push(new Pair<>(queueS.poll(), queueT.poll()));
        }
    }

    private void stopSignal(String type){
        sink.stopSignal(type);
    }

    public ISink<S> getFirstSink(){
        return new ISink<S>() {
            @Override
            public void push(S obj) {
                queueS.add(obj);
                sync();
            }

            @Override
            public void stopSignal(String type) {
                MergePipe.this.stopSignal(type);
            }
        };
    }

    public ISink<T> getSecondSink(){
        return new ISink<T>() {
            @Override
            public void push(T obj) {
                queueT.add(obj);
                sync();
            }

            @Override
            public void stopSignal(String type) {
                MergePipe.this.stopSignal(type);
            }
        };
    }
}
