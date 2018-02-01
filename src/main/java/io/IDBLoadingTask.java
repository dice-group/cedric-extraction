package io;

import com.mongodb.DBObject;


/**
 * An interface for a data loading task in MongoDB.
 */
public interface IDBLoadingTask {

    /**
     *
     * @return configuration for connection
     */
    public IDBConfiguration getConfiguration();

    /**
     * @return name of the collection to load data from
     */
    public String getCollection();

    /**
     * A query which allows to filter the loaded entries.
     * When null is returned, the task will allow all entries.
     * @return a filter query or null
     */
    public DBObject getQuery();

    /**
     *
     * @return a limit for the number of loaded entries.
     */
    public int getLimit();

}
