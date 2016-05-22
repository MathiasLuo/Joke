package com.mathiasluo.joke.model;

import java.util.List;

/**
 * Created by mathiasluo on 16-5-3.
 */
public class JokeEntry {
    public int size;
    public List<String> setups;

    public JokeEntry(int size, List<String> setups) {
        this.size = size;
        this.setups = setups;
    }
}
