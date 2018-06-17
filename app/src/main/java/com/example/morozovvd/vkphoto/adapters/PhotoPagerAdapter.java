package com.example.morozovvd.vkphoto.adapters;

import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.morozovvd.vkphoto.PhotoManager;
import com.example.morozovvd.vkphoto.R;
import com.example.morozovvd.vkphoto.objects.Photo;
import com.example.morozovvd.vkphoto.tasks.ImageDownloadTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;

public class PhotoPagerAdapter extends PagerAdapter {

    private PhotoManager mPhotoManager;
    private OnItemClickListener clickListener;

    public PhotoPagerAdapter(PhotoManager manager) {
        mPhotoManager = manager;
        mPhotoManager.registerObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        if (position == mPhotoManager.getCount() - 1) mPhotoManager.fetchNextPage();
        final Photo photo = mPhotoManager.get(position);
        final ImageView imageView = (ImageView) LayoutInflater.from(container.getContext())
                .inflate(R.layout.item_photo_fullscreen, container, false);


        ImageDownloadTask.ResponseHandler<WeakReference<ImageView>> handler =
                new ImageDownloadTask.ResponseHandler<WeakReference<ImageView>>() {
                    @Override
                    public void onImageDownloaded(Bitmap image, HttpUrl imageUrl, WeakReference<ImageView> callbackParams) {
                        ImageView view = callbackParams.get();
                        if (view != null) {
                            view.setImageBitmap(image);
                        }
                    }
                };

        Photo.Copy fullscreenCopy = getFullscreenCopy(photo);
        String urlString;
        if (fullscreenCopy != null) {
            urlString = fullscreenCopy.getUrl();
            HttpUrl url = HttpUrl.parse(urlString);
            ImageDownloadTask<WeakReference<ImageView>> imageDownloadTask = new ImageDownloadTask<>(
                    url,
                    handler,
                    new WeakReference<>(imageView)
            );
            imageDownloadTask.execute();

            if (clickListener != null) {
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onItemClick(position);
                    }
                });
            }
        } else {
            imageView.setImageResource(R.drawable.ic_broken_image_black_24dp);
        }

        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return imageView;
    }

    @Override
    public int getCount() {
        return mPhotoManager.getCount();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    private Photo.Copy getFullscreenCopy(Photo photo) {
        //todo: вынести в константы (?)
        List<Photo.Copy.Type> fullscreenTypesByPriority = new ArrayList<>();
        fullscreenTypesByPriority.add(Photo.Copy.Type.PROPORTIONAL_1280);
        fullscreenTypesByPriority.add(Photo.Copy.Type.PROPORTIONAL_807);
        fullscreenTypesByPriority.add(Photo.Copy.Type.PROPORTIONAL_604);
        fullscreenTypesByPriority.add(Photo.Copy.Type.PROPORTIONAL_130);
        fullscreenTypesByPriority.add(Photo.Copy.Type.PROPORTIONAL_75);

        Photo.Copy copy = null;
        for (Photo.Copy.Type type : fullscreenTypesByPriority) {
            copy = photo.getCopy(type);
            if (copy != null) break;
        }
        return copy;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
