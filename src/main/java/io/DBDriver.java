package io;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.Arrays;

public class DBDriver {

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
