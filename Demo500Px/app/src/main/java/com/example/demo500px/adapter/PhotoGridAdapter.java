package com.example.demo500px.adapter;

import com.example.demo500px.R;
import com.example.demo500px.network.model.Photo;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * @author Kanghee
 */
public class PhotoGridAdapter extends BaseRecyclerGridAdapter<PhotoGridAdapter.PhotoViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Photo> mPhotos;

    public PhotoGridAdapter(Context context, ArrayList<Photo> photos) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mPhotos = photos;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return mPhotos.get(position).getName().hashCode();
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_photo, viewGroup, false);
        final PhotoViewHolder viewHolder = new PhotoViewHolder(view);
        viewHolder.itemView.setLayoutParams(mImageViewLayoutParams);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        if (mNumColumns == 0) {
            return 0;
        }

        return mPhotos.size();
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder viewHolder, int position) {
        Photo photo = mPhotos.get(position);

        if (mItemHeight > 0) {
            Picasso.with(mContext)
                    .load(photo.getSmallImageUrl())
                    .resize(mItemHeight, mItemHeight)
                    .into(viewHolder.photoView);
        } else {
            Picasso.with(mContext)
                    .load(photo.getSmallImageUrl())
                    .fit()
                    .into(viewHolder.photoView);
        }
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {

        ImageView photoView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            photoView = (ImageView) itemView.findViewById(R.id.img_photo);
        }
    }
}
