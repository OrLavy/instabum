package com.example.or_maayan.instabum.util;

/**
 * Created by orlavy on 4/5/17.
 */

public class Constants {
    private static final Constants ourInstance = new Constants();

    public static Constants getInstance() {
        return ourInstance;
    }

    private Constants() {
    }

    public static final Integer FEED_SIZE = 5;
}
