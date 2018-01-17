package pipeline;

import io.IDBLoadingTask;
import io.IEntityMapper;
import io.MongoLoadingPipe;
import model.ILabelledEntity;

public class RelationExtPipeFactory {

    public IPipe<IDBLoadingTask, ILabelledEntity> createDBLoadingPipe(IEntityMapper mapper){
        return new MongoLoadingPipe(mapper);
    }


}
