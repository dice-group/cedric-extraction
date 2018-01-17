package pipeline;

public interface IPipe<S, T> extends ISink<S>{

    public void setSink(ISink<T> sink);


}
