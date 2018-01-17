package pipeline.async;

import pipeline.ISink;

public class StopEvent<S> {

    private ISink<S> sink;
    private String type;

    public StopEvent(ISink<S> sink, String type) {
        this.sink = sink;
        this.type = type;
    }

    public ISink<S> getSink() {
        return sink;
    }

    public String getType() {
        return type;
    }



}
