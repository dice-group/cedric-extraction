package pipeline;

public interface ISink<T> {

    public void push(T obj);

    public void stopSignal(String type);

}
