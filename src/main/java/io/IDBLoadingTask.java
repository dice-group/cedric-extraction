package io;

import com.mongodb.DBObject;

public interface IDBLoadingTask {

    public IDBConfiguration getConfiguration();

    public String getCollection();

    public DBObject getQuery();

    public int getLimit();

}
