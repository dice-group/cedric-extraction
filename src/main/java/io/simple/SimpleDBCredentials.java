package io.simple;


import io.IDBCredentials;

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
