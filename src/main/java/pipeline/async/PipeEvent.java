package pipeline.async;

import pipeline.ISink;

public class PipeEvent<S> {

    private ISink<S> sink;
    private S obj;

    public PipeEvent(ISink<S> sink, S obj) {
        this.sink = sink;
        this.obj = obj;
    }

    public ISink<S> getSink() {
        return sink;
    }

    public S getObj() {
        return obj;
    }

}
