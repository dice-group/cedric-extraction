package io.simple;

import io.IDBConfiguration;
import io.IDBCredentials;

/**
 *
 * A simple flat implementation for a MongoDB configuration
 *
 * @author Cedric Richter
 */
public class SimpleDBConfiguration implements IDBConfiguration {

    private String hostname;
    private int port;

    private IDBCredentials credentials;

    private String database;

    public SimpleDBConfiguration(String hostname, int port, IDBCredentials credentials, String database) {
        this.hostname = hostname;
        this.port = port;
        this.credentials = credentials;
        this.database = database;
    }

    public SimpleDBConfiguration(String hostname, int port, String database) {
        this(hostname, port, null, database);
    }


    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public IDBCredentials getCredentials() {
        return credentials;
    }

    @Override
    public String getDatabase() {
        return database;
    }
}
