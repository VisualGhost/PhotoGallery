package com.photogallery.networking;


import com.photogallery.util.Util;

public class User {

    String fullname;

    public String getFullName() {
        return Util.getNotNullString(fullname);
    }
}
