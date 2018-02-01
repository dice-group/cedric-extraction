package pipeline;

/**
 * A helper pipe which hides pipes between the input pipe and output pipe
 * @author Cedric Richter
 * @param <S> source type
 * @param <T> target type
 */
public class CompressPipe<S, T> implements IPipe<S, T> {

    private IPipe<S, ?> inPipe;
    private IPipe<?, T> outPipe;

    public CompressPipe(IPipe<S, ?> inPipe, IPipe<?, T> outPipe) {
        this.inPipe = inPipe;
        this.outPipe = outPipe;
    }

    @Override
    public void setSink(ISink<T> sink) {
        outPipe.setSink(sink);
    }

    @Override
    public void push(S obj) {
        inPipe.push(obj);
    }

    @Override
    public void stopSignal(String type) {
        inPipe.stopSignal(type);
    }
}
