package com.photogallery.util;


public class Util {

    public static final String EMPTY = "";

    public static String getNotNullString(String input) {
        return input != null ? input : EMPTY;
    }
}
