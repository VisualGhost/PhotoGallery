package com.photogallery.networking;


import java.util.List;

public interface ParsedModel {

    String getCurrentPageNumber();

    String getTotalPageNumber();

    List<Photo> getPhotoList();

}
