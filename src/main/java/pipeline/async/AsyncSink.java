package pipeline.async;


import pipeline.ISink;

public class AsyncSink<S> implements ISink<S>{

    private ISink<S> org;
    private AsyncSinkManager parent;

    public AsyncSink(ISink<S> org, AsyncSinkManager parent) {
        this.org = org;
        this.parent = parent;
    }

    @Override
    public void push(S obj) {
        parent.pushEvent(new PipeEvent<S>(org, obj));
    }

    @Override
    public void stopSignal(String type) {
        parent.pushEvent(new StopEvent<S>(org, type));
    }

}
