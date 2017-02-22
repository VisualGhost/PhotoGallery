package com.photogallery.view.viewholder;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.photogallery.R;
import com.photogallery.database.DBContractor;
import com.photogallery.util.Util;
import com.photogallery.view.PhotoViewerContract;

public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView mPhotoView;

    private String mPhotoUrl;
    private String mPhotoName;
    private String mCamera;
    private String mUserName;

    public PhotoViewHolder(View itemView) {
        super(itemView);

        mPhotoView = (ImageView) itemView.findViewById(R.id.photo_id);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mPhotoUrl != null) {
            Context context = mPhotoView.getContext();
            Intent intent = new Intent(PhotoViewerContract.ACTION);
            intent.putExtra(PhotoViewerContract.PHOTO_NAME, mPhotoName);
            intent.putExtra(PhotoViewerContract.PHOTO_URL, mPhotoUrl);
            intent.putExtra(PhotoViewerContract.USER_NAME, mUserName);
            intent.putExtra(PhotoViewerContract.CAMERA, mCamera);
            startActivity(context, intent);
        }
    }

    private void startActivity(Context context, Intent intent) {
        String sharedElementName = context.getString(R.string.share_photo);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) context, mPhotoView, sharedElementName);
        context.startActivity(intent, options.toBundle());
    }

    public void bindModel(Cursor cursor) {
        mPhotoUrl = getValue(DBContractor.COLUMN_URL, cursor);
        mPhotoName = getValue(DBContractor.COLUMN_NAME, cursor);
        mCamera = getValue(DBContractor.COLUMN_CAMERA, cursor);
        mUserName = getValue(DBContractor.COLUMN_USER, cursor);
        if (!TextUtils.isEmpty(mPhotoUrl)) {
            Glide
                    .with(mPhotoView.getContext().getApplicationContext())
                    .load(mPhotoUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_photo_dark_gray_24dp)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(mPhotoView);
        }
    }

    private String getValue(String column, Cursor cursor) {
        int index = cursor.getColumnIndex(column);
        if (index != -1) {
            return cursor.getString(index);
        } else {
            return Util.EMPTY;
        }
    }
}
