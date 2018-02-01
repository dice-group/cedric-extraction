package io.config;

import model.IConfiguration;

public class GsonApplicationConfig implements IConfiguration {
    private int window = 2;
    private double bayes = 1.0;
    private int neighbour = 5;

    @Override
    public int getLexicalWindow() {
        return window;
    }

    @Override
    public double getBayesSmoothing() {
        return bayes;
    }

    @Override
    public int getNeighbour() {
        return neighbour;
    }
}
