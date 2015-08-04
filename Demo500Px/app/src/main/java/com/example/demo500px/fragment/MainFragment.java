package com.example.demo500px.fragment;

import com.example.demo500px.R;
import com.example.demo500px.activity.FullScreenActivity;
import com.example.demo500px.adapter.BaseRecyclerGridAdapter;
import com.example.demo500px.adapter.PhotoGridAdapter;
import com.example.demo500px.network.PxApi;
import com.example.demo500px.network.model.Photo;
import com.example.demo500px.network.model.Photos;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A fragment containing a grid of thumbnails
 */
public class MainFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private PhotoGridAdapter mAdapter;
    private ArrayList<Photo> mPhotos;
    private final FutureCallback<Response<Photos>> mCallback = new FutureCallback<Response<Photos>>() {
        @Override
        public void onCompleted(Exception e, Response<Photos> result) {
            if (e != null) {
                mCurrentPage--;
                Toast.makeText(getSupportActivity(), "Please check the internet connection",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Photos photos = result.getResult();
            if (photos != null) {
                mTotalPages = photos.getTotalPages();
                populatePhotosOnUiThread(photos.getPhotos());
            }
            mIsLoadingMore = false;
        }
    };

    private int mCurrentPage = 1, mTotalPages;
    private boolean mIsLoadingMore = false;
    private View mLoadingView;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        getSupportActivity().setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));
        mRecyclerView = (RecyclerView) view.findViewById(R.id.grid_thumbnails);
        int numColumns = getResources().getInteger(R.integer.num_columns_thumbnail);
        final GridLayoutManager layoutManager = new GridLayoutManager(getSupportActivity(), numColumns);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int firstVisibleItem, visibleItemCount, totalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mAdapter == null) {
                    return;
                }

                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                visibleItemCount = mRecyclerView.getChildCount();
                totalItemCount = layoutManager.getItemCount();

                if (mCurrentPage < mTotalPages) {
                    boolean loadMore =
                            (totalItemCount - visibleItemCount) <= (firstVisibleItem + 5);
                    if (loadMore && !mIsLoadingMore && dy > 0) {
                        mIsLoadingMore = true;
                        mCurrentPage++;
                        PxApi.getPhotos(MainFragment.this, mCurrentPage, mCallback);
                    }
                }
            }
        });

        mLoadingView = view.findViewById(R.id.loading_view);

        PxApi.getPhotos(this, mCurrentPage, mCallback);
        return view;
    }

    private void populatePhotosOnUiThread(final ArrayList<Photo> photos) {
        if (!isAdded()) {
            return;
        }

        getSupportActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoadingView.getVisibility() == View.VISIBLE) {
                    mLoadingView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }

                if (photos != null && !photos.isEmpty()) {
                    if (mAdapter == null) {
                        mPhotos = photos;
                        mAdapter = new PhotoGridAdapter(getSupportActivity(), mPhotos);
                        mAdapter.setOnItemClickListener(
                                new BaseRecyclerGridAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        FullScreenActivity.showImageInFullScreenWithPos(MainFragment.this,
                                                position, mPhotos, mCurrentPage);
                                    }
                                });
                        mRecyclerView.setAdapter(mAdapter);
                        setupRecyclerViewGrid(mRecyclerView, 0);
                    } else {
                        if (!mIsLoadingMore) {
                            mPhotos.clear();
                        }
                        mPhotos.addAll(photos);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void setupRecyclerViewGrid(final RecyclerView gridView, final int padding) {
        // This listener is used to get the final width of the RecyclerView and then calculate the
        // number of columns and the width of each column. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        gridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int numColumns = ((GridLayoutManager) gridView.getLayoutManager())
                                .getSpanCount();
                        int orientation =
                                ((GridLayoutManager) gridView.getLayoutManager()).getOrientation();

                        if (numColumns > 0) {
                            int columnWidth;
                            if (orientation == GridLayoutManager.VERTICAL) {
                                columnWidth = (gridView.getWidth() - gridView.getPaddingLeft() -
                                        gridView.getPaddingRight() - (padding * (numColumns - 1))) / numColumns;
                            } else {
                                columnWidth = (gridView.getHeight() - gridView.getPaddingTop() -
                                        gridView.getPaddingBottom() - (padding * (numColumns - 1))) / numColumns;
                            }
                            mAdapter.setNumColumns(numColumns);
                            mAdapter.setItemHeight(columnWidth);
                            gridView.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        }
                    }
                });
    }
}
