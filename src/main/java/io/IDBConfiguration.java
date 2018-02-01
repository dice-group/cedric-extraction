package io;

/**
 *
 * An interface for MongoDB configurations
 *
 * @author Cedric Richter
 */
public interface IDBConfiguration {

    /**
     *
     * @return the host URL for the server
     */
    public String getHostname();

    /**
     *
     * @return the port under which the db can be requested
     */
    public int getPort();

    /**
     *
     * @return login credentials
     */
    public IDBCredentials getCredentials();

    /**
     *
     * @return authentication and usage database
     */
    public String getDatabase();


}
