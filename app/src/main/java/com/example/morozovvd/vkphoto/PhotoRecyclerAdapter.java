package com.example.morozovvd.vkphoto;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class PhotoRecyclerAdapter extends RecyclerView.Adapter {

    private List<Bitmap> mBitmapList;

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;

        PhotoViewHolder(ImageView imageView) {
            super(imageView);
            mImageView = imageView;
        }
    }

    public PhotoRecyclerAdapter() {
        this.mBitmapList = new ArrayList<>();
    }

    public void setPhotos(List<Bitmap> bitmaps) {
        mBitmapList.clear();
        mBitmapList.addAll(bitmaps);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView view = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
        photoViewHolder.mImageView.setImageBitmap(mBitmapList.get(position));
    }

    @Override
    public int getItemCount() {
        return mBitmapList.size();
    }
}
