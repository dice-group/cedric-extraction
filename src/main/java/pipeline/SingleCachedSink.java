package pipeline;

public class SingleCachedSink<T> implements ISink<T> {

    private T obj;

    @Override
    public void push(T obj) {
        this.obj = obj;
    }

    @Override
    public void stopSignal(String type) {
    }

    public T getObj() {
        return obj;
    }
}
