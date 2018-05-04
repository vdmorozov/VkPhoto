package com.example.morozovvd.vkphoto;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.morozovvd.vkphoto.objects.Photo;
import com.example.morozovvd.vkphoto.tasks.ImageDownloadTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;

import static com.example.morozovvd.vkphoto.activities.MainActivity.COPY_TYPE_FOR_PREVIEW;

public class PhotoRecyclerAdapter extends RecyclerView.Adapter {

    private List<Photo> mPhotoList;
    private Map<Integer, Bitmap> mBitmapCache;

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;

        PhotoViewHolder(ImageView imageView) {
            super(imageView);
            mImageView = imageView;
        }
    }

    public PhotoRecyclerAdapter() {
        this.mPhotoList = new ArrayList<>();
        this.mBitmapCache = new HashMap<>();
    }

    public void setPhotos(List<Photo> photos) {
        mPhotoList.clear();
        mPhotoList.addAll(photos);
        notifyDataSetChanged();
    }

    public void addPhotos(List<Photo> photos) {
        mPhotoList.addAll(photos);
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
        final int index = position;
        final PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
        final ImageView imageView = photoViewHolder.mImageView;
        final Photo photo = mPhotoList.get(position);

        //todo: убрать говнокодище

        Bitmap bitmap = null;
        try {
            bitmap = mBitmapCache.get(position);
        } catch (IndexOutOfBoundsException ignored) { }

        if (bitmap != null) imageView.setImageBitmap(bitmap);
        else {
            ImageDownloadTask.ResponseHandler handler = new ImageDownloadTask.ResponseHandler() {
                @Override
                public void onImageDownloaded(Bitmap image, int imageId, HttpUrl imageUrl) {
                    imageView.setImageBitmap(image);
                    mBitmapCache.put(index, image);
                }
            };

            String urlString = photo.getCopy(COPY_TYPE_FOR_PREVIEW).getUrl();
            HttpUrl url = HttpUrl.parse(urlString);
            ImageDownloadTask imageDownloadTask = new ImageDownloadTask(
                    url,
                    0,
                    new WeakReference<>(handler)
            );

            imageDownloadTask.execute();
        }
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }
}
