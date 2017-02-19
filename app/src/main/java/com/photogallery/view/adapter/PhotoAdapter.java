package com.photogallery.view.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.photogallery.R;
import com.photogallery.networking.Photo;
import com.photogallery.view.viewholder.PhotoCellController;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoCellController> {

    private List<Photo> mPhotoList;

    public void setPhotoList(List<Photo> photoList) {
        mPhotoList = photoList;
        notifyDataSetChanged();
    }

    @Override
    public PhotoCellController onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoCellController(LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_cell,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(PhotoCellController holder, int position) {
        holder.bindModel(mPhotoList.get(position));
    }

    @Override
    public int getItemCount() {
        return mPhotoList != null ? mPhotoList.size() : 0;
    }
}
