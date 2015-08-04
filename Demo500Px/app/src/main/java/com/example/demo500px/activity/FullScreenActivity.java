package com.example.demo500px.activity;

import com.example.demo500px.R;
import com.example.demo500px.network.PxApi;
import com.example.demo500px.network.model.Photo;
import com.example.demo500px.network.model.Photos;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Created by Kanghee on 15-08-03.
 */
public class FullScreenActivity extends AppCompatActivity {

    public static final int REQUEST_FULL_SCREEN_IMAGE = 10;
    private static final String EXTRA_POSITION = "extra_position";
    private static final String EXTRA_PHOTOS = "extra_photos";
    private static final String EXTRA_CURRENT_PAGE = "extra_current_page";

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private ArrayList<Photo> mPhotos;
    private int mCurrentPage , mTotalPages;
    private boolean mIsLoadingMore;

    private final FutureCallback<Response<Photos>> mCallback = new FutureCallback<Response<Photos>>() {
        @Override
        public void onCompleted(Exception e, Response<Photos> result) {
            if (e != null) {
                mCurrentPage--;
                return;
            }

            Photos photos = result.getResult();
            if (photos != null) {
                mTotalPages = photos.getTotalPages();
                mPhotos.addAll(photos.getPhotos());
                mAdapter.updatePhotos(mPhotos);
            }
            mIsLoadingMore = false;
        }
    };

    public static void showImageInFullScreenWithPos(Fragment fragment, int pos,
            ArrayList<Photo> photos, int currentPage) {
        Intent intent = new Intent(fragment.getActivity(), FullScreenActivity.class);
        intent.putExtra(EXTRA_POSITION, pos);
        intent.putExtra(EXTRA_PHOTOS, photos);
        intent.putExtra(EXTRA_CURRENT_PAGE, currentPage);
        fragment.startActivityForResult(intent, REQUEST_FULL_SCREEN_IMAGE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        Intent intent = getIntent();
        int position = intent.getIntExtra(EXTRA_POSITION, 0);
        mPhotos = (ArrayList<Photo>) intent.getSerializableExtra(EXTRA_PHOTOS);
        mCurrentPage = intent.getIntExtra(EXTRA_CURRENT_PAGE, 0);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(mPhotos.get(position).getName());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mAdapter = new ViewPagerAdapter(this, mPhotos);
        mViewPager.setAdapter(mAdapter);
        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mToolbar.setTitle(mPhotos.get(position).getName());
                if (mCurrentPage < mTotalPages) {
                    int totalSize = mPhotos.size();
                    if (position >= totalSize - 3 && position < totalSize && !mIsLoadingMore) {
                        mIsLoadingMore = true;
                        mCurrentPage++;
                        PxApi.getPhotos(FullScreenActivity.this, mCurrentPage, mCallback);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        mViewPager.addOnPageChangeListener(pageChangeListener);
        mViewPager.setCurrentItem(position);
        pageChangeListener.onPageSelected(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class ViewPagerAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<Photo> mPhotos;
        private LayoutInflater mInflater;

        public ViewPagerAdapter(Context context, ArrayList<Photo> photos) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mPhotos = photos;
        }

        public void updatePhotos(ArrayList<Photo> photos) {
            mPhotos = photos;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mPhotos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View view = mInflater.inflate(R.layout.item_full_screen_image, null);
            ImageView imgAlbumArt = (ImageView) view.findViewById(R.id.img_photo);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            Photo photo = mPhotos.get(position);
            Picasso.with(mContext)
                    .load(photo.getLargeImageUrl())
                    .into(imgAlbumArt, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });

            view.setTag(photo);
            ((ViewPager) container).addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            Picasso.with(mContext).cancelRequest((ImageView) view.findViewById(R.id.img_photo));
            ((ViewPager) container).removeView(view);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public int getItemPosition(Object object) {
            View view = (View) object;
            Photo photo = (Photo) view.getTag();
            int position = photo != null ? mPhotos.indexOf(photo) : -1;
            return position >= 0 ? position : POSITION_NONE;
        }
    }
}

