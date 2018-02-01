package pipeline;

/**
 * A simple sink which can only save one entry.
 * Therefore when a new object arrives, the last one is deleted.
 * @author Cedric Richter
 * @param <T> save type
 */
public class SingleCachedSink<T> implements ISink<T> {

    private T obj;

    @Override
    public void push(T obj) {
        this.obj = obj;
    }

    @Override
    public void stopSignal(String type) {
    }

    /**
     *
     * @return the last saved object in the sink
     */
    public T getObj() {
        return obj;
    }
}
