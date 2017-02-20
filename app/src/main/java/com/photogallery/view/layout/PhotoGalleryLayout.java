package com.photogallery.view.layout;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.photogallery.R;
import com.photogallery.util.DebugLogger;
import com.photogallery.view.pagination.PaginationListener;


public class PhotoGalleryLayout extends RelativeLayout {

    private static final String TAG = PhotoGalleryLayout.class.getSimpleName();
    private static final int THRESHOLD = 5;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private PaginationListener mPaginationListener;

    private boolean mIsWaitingItems;

    private int mTotalPage;
    private int mCurrentPage;

    public PhotoGalleryLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.photo_grid, this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_id);
        mLayoutManager = LinearLayoutManagerFabric.createLayoutManager(LinearLayoutManagerFabric.GRID, context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        initScrollListener();
        managePagination();
    }

    private void initScrollListener() {
        mOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //check for scroll down
                {
                    if (isNeedMoreItems() && !mIsWaitingItems) {
                        mIsWaitingItems = true;
                        DebugLogger.d(TAG, "Load next page");
                        if (mPaginationListener != null) {
                            mPaginationListener.loadPage(mCurrentPage + 1);
                        }
                    }
                }
            }
        };
    }

    private void managePagination() {
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    private boolean isNeedMoreItems() {
        int totalItemCount = mLayoutManager.getItemCount();
        int pastVisibleItems = mLayoutManager.findLastVisibleItemPosition();

        return mCurrentPage < mTotalPage && pastVisibleItems >= (totalItemCount - THRESHOLD);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        DebugLogger.d(TAG, "onAttachedToWindow");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DebugLogger.d(TAG, "onDetachedFromWindow");
        mRecyclerView.removeOnScrollListener(mOnScrollListener);
        mPaginationListener = null;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setCurrentPage(String page) {
        int newPage = Integer.parseInt(page);
        boolean isNewItems = mCurrentPage < newPage;
        DebugLogger.d(TAG, "old page: " + mCurrentPage + ", current page: " + page);
        mIsWaitingItems = !isNewItems;
        mCurrentPage = newPage;
    }

    public void setTotalPage(String page) {
        mTotalPage = Integer.parseInt(page);
    }

    public void setPaginationListener(PaginationListener paginationListener) {
        mPaginationListener = paginationListener;
    }
}
