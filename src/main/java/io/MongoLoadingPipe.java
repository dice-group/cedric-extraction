package io;

import com.mongodb.*;
import model.ILabelledEntity;
import pipeline.APipe;


import java.net.UnknownHostException;

public class MongoLoadingPipe extends APipe<IDBLoadingTask, ILabelledEntity> {

    public static final String STOP_SIGNAL = "mongo";

    private IEntityMapper mapper;

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
                int count = 0;
                while(cursor.hasNext() && count < obj.getLimit()){
                    DBObject dbO = cursor.next();
                    ILabelledEntity entity = mapper.mapEntity(dbO);

                    if(entity != null) {
                        sink.push(entity);
                        count++;
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
