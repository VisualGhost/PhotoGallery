package com.photogallery.networking;


import com.photogallery.util.Util;

import java.util.Collections;
import java.util.List;

class ReceivedData {

    String current_page;
    String total_pages;

    List<Photo> photos;

    String getCurrentPage() {
        return Util.getNotNullString(current_page);
    }

    String getTotalPages() {
        return Util.getNotNullString(total_pages);
    }

    List<Photo> getPhotos() {
        return photos != null ? photos : Collections.<Photo>emptyList();
    }
}
