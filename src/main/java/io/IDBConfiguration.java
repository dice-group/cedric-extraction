package io;

public interface IDBConfiguration {

    public String getHostname();

    public int getPort();

    public IDBCredentials getCredentials();

    public String getDatabase();


}
