package pipeline;

public class CastPipe<S, T> extends APipe<S, T>{
    @Override
    public void push(S obj) {
        try {
            sink.push((T)obj);
        }catch(ClassCastException e){}
    }
}
