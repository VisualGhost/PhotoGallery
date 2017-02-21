package com.photogallery.networking;

import java.util.Collections;
import java.util.List;

class ParsedModelImpl implements ParsedModel {

    private ReceivedData mReceivedData;
    private boolean mIsDataValid;

    private static final String INVALID_PAGE_NUMBER = "-1";

    ParsedModelImpl(ReceivedData receivedData) {
        mReceivedData = receivedData;
        mIsDataValid = mReceivedData != null;
    }

    @Override
    public String getCurrentPageNumber() {
        return mReceivedData != null ? mReceivedData.getCurrentPage() : INVALID_PAGE_NUMBER;
    }

    @Override
    public String getTotalPageNumber() {
        return mReceivedData != null ? mReceivedData.getTotalPages() : INVALID_PAGE_NUMBER;
    }

    @Override
    public List<Photo> getPhotoList() {
        return mReceivedData != null ? mReceivedData.getPhotos() : Collections.<Photo>emptyList();
    }

    @Override
    public boolean isDataValid() {
        return mIsDataValid;
    }
}
