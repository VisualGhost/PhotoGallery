package com.photogallery.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.photogallery.R;


public class PhotoGridLayout extends RelativeLayout implements PhotoGrid {

    private RecyclerView mRecyclerView;

    public PhotoGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.photo_grid, this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_id);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, getResources().getInteger(R.integer.cell_number)));
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }
}
