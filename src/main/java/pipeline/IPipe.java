package pipeline;

/**
 * A pipe is a sink which processes the input data and pushes the result to the chained sink.
 * @author Cedric Richter
 * @param <S> source type
 * @param <T> target type
 */
public interface IPipe<S, T> extends ISink<S>{

    /**
     * Sets the sink where the pipe should push to
     * @param sink a sink for output
     */
    public void setSink(ISink<T> sink);


}
