package com.photogallery.view.adapter;


import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.photogallery.R;
import com.photogallery.view.viewholder.PhotoViewHolder;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

    private Cursor mCursor;

    public void setData(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_cell,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.bindModel(mCursor);
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }
}
