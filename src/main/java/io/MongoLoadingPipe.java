package io;

import com.mongodb.*;
import model.ILabelledEntity;
import pipeline.APipe;


import java.net.UnknownHostException;

/**
 * A pipe which takes a loading task as input and then push read entities (mapped
 * as labelled entities).
 *
 * The pipe emit a object for each entries (for one loading task there are N entities emitted).
 * When the task is finished the pipe emits a stop signal.
 *
 * @author Cedric Richter
 */
public class MongoLoadingPipe extends APipe<IDBLoadingTask, ILabelledEntity> {

    public static final String STOP_SIGNAL = "mongo";

    private IEntityMapper mapper;

    /**
     *
     * @param mapper a mapper which used to map MongoDB entries to labelled entities
     */
    public MongoLoadingPipe(IEntityMapper mapper) {
        this.mapper = mapper;
    }


    @Override
    public void push(IDBLoadingTask obj) {

        MongoClient client = null;

        try {
            client = DBDriver.connectMongo(obj.getConfiguration());

            DB db = client.getDB(obj.getConfiguration().getDatabase());

            DBCollection collection = db.getCollection(obj.getCollection());

            DBCursor cursor;

            if(obj.getQuery() != null){
                cursor = collection.find(obj.getQuery());
            }else{
                cursor = collection.find();
            }

            try{
                int length = cursor.count();
                double last = 0.0;

                int count = 0;
                while(cursor.hasNext() && count < obj.getLimit()){
                    DBObject dbO = cursor.next();
                    ILabelledEntity entity = mapper.mapEntity(dbO);

                    if(entity != null) {
                        sink.push(entity);
                        count++;

                        if(((double)count/length)-last >=0.1){
                            last = ((double)count/length);
                            System.out.println("Processed "+(int)(last*100)+"% of data ( "+count+" / "+length+" )");
                        }
                    }
                }
            }finally{
                cursor.close();
            }

            sink.stopSignal(MongoLoadingPipe.STOP_SIGNAL);

        } catch (UnknownHostException e) {
           e.printStackTrace();
        }finally{
            if(client != null)
                client.close();
        }

    }
}
