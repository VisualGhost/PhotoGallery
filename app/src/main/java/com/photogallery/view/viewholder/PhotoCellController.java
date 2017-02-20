package com.photogallery.view.viewholder;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.photogallery.PhotoViewerActivity;
import com.photogallery.R;
import com.photogallery.networking.Photo;
import com.photogallery.view.PhotoViewerContract;

public class PhotoCellController extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView mPhoto;

    private String mPhotoUrl;
    private String mPhotoName;
    private String mCamera;
    private String mUserName;

    public PhotoCellController(View itemView) {
        super(itemView);

        mPhoto = (ImageView) itemView.findViewById(R.id.photo_id);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mPhotoUrl != null) {
            Context context = mPhoto.getContext();
            Intent intent = new Intent(PhotoViewerContract.ACTION);
            intent.putExtra(PhotoViewerContract.PHOTO_NAME, mPhotoName);
            intent.putExtra(PhotoViewerContract.PHOTO_URL, mPhotoUrl);
            intent.putExtra(PhotoViewerContract.USER_NAME, mUserName);
            intent.putExtra(PhotoViewerContract.CAMERA, mCamera);
            context.startActivity(intent);
        }
    }

    public void bindModel(Photo photo) {
        mPhotoUrl = photo.getImageUrl();
        mPhotoName = photo.getName();
        mCamera = photo.getCamera();
        mUserName = photo.getUser().getFullName();
        if (!TextUtils.isEmpty(mPhotoUrl)) {
            Glide
                    .with(mPhoto.getContext())
                    .load(mPhotoUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_photo_dark_gray_24dp)
                    .crossFade()
                    .into(mPhoto);
        }
    }
}
