package io;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * A driver for creating new connections to a MongoDB.
 * @author Cedric Richter
 */
public class DBDriver {

    /**
     * Creates a new Mongo Client based on the given configuration.
     * @param config a connection configuration
     * @return a Mongo Client
     * @throws UnknownHostException if the given host is unknown
     */
    public static MongoClient connectMongo(IDBConfiguration config) throws UnknownHostException {
        if(config.getCredentials() != null){
            IDBCredentials dbCredentials = config.getCredentials();
            MongoCredential cred = MongoCredential.createCredential(
                    dbCredentials.getName(),
                    config.getDatabase(),
                    dbCredentials.getPassword()
                    );

            return new MongoClient(new ServerAddress(config.getHostname(), config.getPort()),
                    Arrays.asList(cred));
        }else{
            return new MongoClient(config.getHostname(), config.getPort());
        }
    }

}
