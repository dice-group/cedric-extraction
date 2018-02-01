package pipeline;

/**
 * An abstract implementation for IPipe.
 * Only the push method has to be implemented.
 * @param <S> source type
 * @param <T> target type
 * @author Cedric Richter
 */
public abstract class APipe<S, T> implements IPipe<S, T> {

    protected ISink<T> sink;

    @Override
    public void setSink(ISink<T> sink) {
        this.sink = sink;
    }

    @Override
    public void stopSignal(String type) {
        sink.stopSignal(type);
    }


}
