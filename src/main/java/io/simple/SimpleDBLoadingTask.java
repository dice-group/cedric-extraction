package io.simple;

import com.mongodb.DBObject;
import io.IDBConfiguration;
import io.IDBLoadingTask;

/**
 *
 * A simple flat implementation for a MongoDB task
 *
 * @author Cedric Richter
 */
public class SimpleDBLoadingTask implements IDBLoadingTask {

    private IDBConfiguration configuration;
    private String collection;
    private DBObject query;

    private int limit;

    public SimpleDBLoadingTask(IDBConfiguration configuration, String collection, DBObject query, int limit) {
        this.configuration = configuration;
        this.collection = collection;
        this.query = query;
        this.limit = limit;
    }

    public SimpleDBLoadingTask(IDBConfiguration configuration, String collection, DBObject query){
        this(configuration, collection, query, Integer.MAX_VALUE);
    }

    public SimpleDBLoadingTask(IDBConfiguration config, String collection){
        this(config, collection, null);
    }



    @Override
    public IDBConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public String getCollection() {
        return collection;
    }

    @Override
    public DBObject getQuery() {
        return query;
    }

    @Override
    public int getLimit() {
        return limit;
    }
}
