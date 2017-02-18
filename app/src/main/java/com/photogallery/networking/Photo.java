package com.photogallery.networking;


import com.photogallery.util.Util;

public class Photo {

    private static final User DUMMY_USER = new User();

    String name;
    String camera;
    String image_url;
    User user;

    public String getName() {
        return Util.getNotNullString(name);
    }

    public String getCamera() {
        return Util.getNotNullString(camera);
    }

    public String getImageUrl() {
        return Util.getNotNullString(image_url);
    }

    public User getUser() {
        return user != null ? user : DUMMY_USER;
    }
}
