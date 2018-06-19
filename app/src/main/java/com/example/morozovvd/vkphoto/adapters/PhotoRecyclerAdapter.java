package com.example.morozovvd.vkphoto.adapters;

import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.morozovvd.vkphoto.PhotoManager;
import com.example.morozovvd.vkphoto.R;
import com.example.morozovvd.vkphoto.objects.Photo;
import com.example.morozovvd.vkphoto.tasks.ImageDownloadTask;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;

import static com.example.morozovvd.vkphoto.activities.MainActivity.COPY_TYPE_FOR_PREVIEW;

public class PhotoRecyclerAdapter extends RecyclerView.Adapter {

    private PhotoManager mPhotoManager;
    private OnPhotoClickListener onPhotoClickListener;

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;

        PhotoViewHolder(ImageView imageView) {
            super(imageView);
            mImageView = imageView;
        }
    }

    public PhotoRecyclerAdapter(PhotoManager manager) {
        mPhotoManager = manager;
        manager.registerObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }
        });
    }

    public void addOnPhotoClickListener(OnPhotoClickListener listener) {
        onPhotoClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView view = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_preview, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
        final ImageView imageView = photoViewHolder.mImageView;
        final Photo photo = mPhotoManager.get(position);

        //todo: вынести задание listener-а в метод addOnPhotoClickListener()
        //todo: попробовать перенести связывание listener-а с фоткой внутрь ViewHolder
        //todo: лучше понять, почему надо использовать holder.getAdapterPosition()

        imageView.setImageBitmap(null);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPhotoClickListener != null) {
                    onPhotoClickListener.onPhotoClick(photo, holder.getAdapterPosition());
                }
            }
        });

        //todo: убрать это говнокодище

        Bitmap cached = mPhotoManager.getThumbnailCache().get(position);
        if (cached != null) {
            imageView.setImageBitmap(cached);
        } else {
            ImageDownloadTask.ResponseHandler<WeakReference<ImageView>> handler =
                    new ImageDownloadTask.ResponseHandler<WeakReference<ImageView>>() {
                        @Override
                        public void onImageDownloaded(Bitmap image, HttpUrl imageUrl, WeakReference<ImageView> callbackParams) {
                            mPhotoManager.getThumbnailCache().put(position, image);
                            ImageView view = callbackParams.get();
                            if (view != null) {
                                view.setImageBitmap(image);
                            }
                        }
                    };

            String urlString = photo.getCopy(COPY_TYPE_FOR_PREVIEW).getUrl();
            HttpUrl url = HttpUrl.parse(urlString);
            ImageDownloadTask<WeakReference<ImageView>> imageDownloadTask = new ImageDownloadTask<>(
                    url,
                    handler,
                    new WeakReference<>(imageView)
            );

            imageDownloadTask.execute();
        }
    }

    @Override
    public int getItemCount() {
        return mPhotoManager.getCount();
    }

    public interface OnPhotoClickListener {
        void onPhotoClick(Photo photo, int position);
    }
}
