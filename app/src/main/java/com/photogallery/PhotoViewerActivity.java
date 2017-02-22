package com.photogallery;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import static com.photogallery.view.PhotoViewerContract.CAMERA;
import static com.photogallery.view.PhotoViewerContract.PHOTO_NAME;
import static com.photogallery.view.PhotoViewerContract.PHOTO_URL;
import static com.photogallery.view.PhotoViewerContract.USER_NAME;

public class PhotoViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_viewer);
        Intent intent = getIntent();

        TextView photoName = (TextView) findViewById(R.id.photo_name_id);
        photoName.setText(intent.getExtras().getString(PHOTO_NAME));

        TextView camera = (TextView) findViewById(R.id.camera_id);
        String cameraModel = intent.getExtras().getString(CAMERA);
        if (!TextUtils.isEmpty(cameraModel)) {
            camera.setText(cameraModel);
        } else {
            camera.setVisibility(View.GONE);
        }

        TextView userName = (TextView) findViewById(R.id.user_name_id);
        userName.setText(intent.getExtras().getString(USER_NAME));

        ImageView imageView = (ImageView) findViewById(R.id.photo_viewer_id);
        final String url = intent.getExtras().getString(PHOTO_URL);
        Glide
                .with(this.getApplicationContext())
                .load(url)
                .fitCenter()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.photo_share_id);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, url);
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_photo_extra_subject));
                startActivity(Intent.createChooser(intent, getString(R.string.share_photo)));
            }
        });
    }
}
