package com.photogallery;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class PhotoViewerActivity extends AppCompatActivity {

    public static final String PHOTO_URL = "photo_url";
    public static final String PHOTO_NAME = "photo_name";
    public static final String USER_NAME = "user_name";
    public static final String CAMERA = "camera";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        ImageView imageView = (ImageView) findViewById(R.id.photo_viewer_id);
        final String url = getIntent().getExtras().getString(PHOTO_URL);
        Glide
                .with(this)
                .load(url)
                .fitCenter()
                .crossFade()
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
