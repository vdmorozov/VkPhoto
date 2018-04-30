package com.example.morozovvd.vkphoto;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.morozovvd.vkphoto.objects.Photo;

import java.util.List;

public class PhotoRecyclerAdapter extends RecyclerView.Adapter {

    private int[] photoResourceIds;

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;

        PhotoViewHolder(ImageView imageView) {
            super(imageView);
            mImageView = imageView;
        }
    }

    public PhotoRecyclerAdapter(int[] photoResourceIds) {
        this.photoResourceIds = photoResourceIds;
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
        photoViewHolder.mImageView.setImageResource(photoResourceIds[position]);
    }

    @Override
    public int getItemCount() {
        return photoResourceIds.length;
    }
}
