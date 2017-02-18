package com.photogallery.networking;

import java.util.Collections;
import java.util.List;

class ParsedModelImpl implements ParsedModel {

    private ReceivedData mReceivedData;

    private static final String INVALID_PAGE_NUMBER = "-1";

    ParsedModelImpl(ReceivedData receivedData) {
        mReceivedData = receivedData;
    }

    @Override
    public String getCurrentPageNumber() {
        return mReceivedData != null ? mReceivedData.current_page : INVALID_PAGE_NUMBER;
    }

    @Override
    public String getTotalPageNumber() {
        return mReceivedData != null ? mReceivedData.total_pages : INVALID_PAGE_NUMBER;
    }

    @Override
    public List<Photo> getPhotoList() {
        return mReceivedData != null ? mReceivedData.photos : Collections.<Photo>emptyList();
    }
}
