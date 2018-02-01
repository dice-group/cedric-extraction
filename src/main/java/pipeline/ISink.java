package pipeline;

/**
 * A sink is an object which can receive data.
 * @author Cedric Richter
 * @param <T> save type
 */
public interface ISink<T> {

    /**
     *
     * @param obj an object to process
     */
    public void push(T obj);

    /**
     * A stop signal will be triggered if the source finished loading data
     * @param type type of the source
     */
    public void stopSignal(String type);

}
