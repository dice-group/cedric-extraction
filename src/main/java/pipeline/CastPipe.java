package pipeline;

/**
 * A pipe which cast the input of type S to an object of type T and emits it
 * @author Cedric Richter
 * @param <S> source type
 * @param <T> target type
 */
public class CastPipe<S, T> extends APipe<S, T>{
    @Override
    public void push(S obj) {
        try {
            sink.push((T)obj);
        }catch(ClassCastException e){}
    }
}
