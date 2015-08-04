package com.example.demo500px.activity;

import com.example.demo500px.R;
import com.example.demo500px.network.PxApi;
import com.example.demo500px.network.model.Photo;
import com.example.demo500px.network.model.Photos;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Response;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Created by Kanghee on 15-08-03.
 */
public class FullScreenActivity extends AppCompatActivity {

    public static final int REQUEST_FULL_SCREEN_IMAGE = 10;
    public static final String EXTRA_POSITION = "extra_position";
    public static final String EXTRA_PHOTOS = "extra_photos";
    public static final String EXTRA_CURRENT_PAGE = "extra_current_page";

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

    public static void showImageInFullScreenWithPos(Activity activity, int pos,
            ArrayList<Photo> photos, int currentPage, ImageView imageView) {
        Intent intent = new Intent(activity, FullScreenActivity.class);
        intent.putExtra(EXTRA_POSITION, pos);
        intent.putExtra(EXTRA_PHOTOS, photos);
        intent.putExtra(EXTRA_CURRENT_PAGE, currentPage);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                imageView, activity.getString(R.string.photo_transition_name));
        ActivityCompat.startActivityForResult(activity, intent, REQUEST_FULL_SCREEN_IMAGE, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        Window window = getWindow();
        window.setEnterTransition(fade);
        window.setExitTransition(fade);
        TransitionSet
                transitionSet = new TransitionSet().addTransition(new ChangeImageTransform())
                .addTransition(new ChangeBounds());
        window.setSharedElementEnterTransition(transitionSet);
        window.setSharedElementExitTransition(transitionSet);
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
        mAdapter.setOnImageLoadedListener(new ViewPagerAdapter.OnImageLoadedListener() {
            @Override
            public void onImageLoaded() {
                supportStartPostponedEnterTransition();
            }
        });
        mViewPager.setAdapter(mAdapter);
        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mToolbar.setTitle(mPhotos.get(position).getName());
                if (mTotalPages == 0 || mCurrentPage < mTotalPages) {
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
        supportPostponeEnterTransition();
    }

    @Override
    public void onBackPressed() {
        populateActivityResult();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                populateActivityResult();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateActivityResult() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PHOTOS, mPhotos);
        intent.putExtra(EXTRA_CURRENT_PAGE, mCurrentPage);
        intent.putExtra(EXTRA_POSITION, mViewPager.getCurrentItem());
        setResult(RESULT_OK, intent);
    }

    private static class ViewPagerAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<Photo> mPhotos;
        private LayoutInflater mInflater;
        public interface OnImageLoadedListener {
            void onImageLoaded();
        }
        private OnImageLoadedListener mImageLoadedListener;

        public ViewPagerAdapter(Context context, ArrayList<Photo> photos) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mPhotos = photos;
        }

        public void updatePhotos(ArrayList<Photo> photos) {
            mPhotos = photos;
            notifyDataSetChanged();
        }

        public void setOnImageLoadedListener(OnImageLoadedListener listener) {
            mImageLoadedListener = listener;
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
                            mImageLoadedListener.onImageLoaded();
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            mImageLoadedListener.onImageLoaded();
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

