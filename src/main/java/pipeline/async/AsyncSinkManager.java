package pipeline.async;

import pipeline.IPipe;
import pipeline.ISink;

public class AsyncSinkManager {

    public <S> AsyncSink<S> makeAsync(ISink<S> sink){
        return new AsyncSink<S> (sink, this);
    }


    void pushEvent(Object o){

    }

}
