package io.simple;


import io.IDBCredentials;

/**
 *
 * A simple flat implementation for MongoDB credentials
 *
 * @author Cedric Richter
 */
public class SimpleDBCredentials implements IDBCredentials{
    private String name;
    private char[] password;


    public SimpleDBCredentials(String name, char[] password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public char[] getPassword() {
        return password;
    }
}
