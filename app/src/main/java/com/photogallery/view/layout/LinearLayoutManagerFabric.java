package com.photogallery.view.layout;


import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;

import com.photogallery.R;

class LinearLayoutManagerFabric {

    static final int GRID = 0;

    static LinearLayoutManager createLayoutManager(int type, Context context) {
        switch (type) {
            case GRID:
                return new GridLayoutManager(context, context.getResources().getInteger(R.integer.cell_number));
            default:
                return new LinearLayoutManager(context);
        }
    }

}
