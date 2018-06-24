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
import com.example.morozovvd.vkphoto.objects.PhotoMeta;
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
        final PhotoMeta photoMeta = mPhotoManager.get(position);
        final ImageView imageView = (ImageView) LayoutInflater.from(container.getContext())
                .inflate(R.layout.item_photo_fullscreen, container, false);

        if (clickListener != null) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(position);
                }
            });
        }

        //todo: привести в порядок это дерьмо
        Bitmap cached = mPhotoManager.getFullscreenCache().get(position);
        if (cached != null) {
            imageView.setImageBitmap(cached);
        } else {
            ImageDownloadTask.ResponseHandler<WeakReference<ImageView>> handler =
                    new ImageDownloadTask.ResponseHandler<WeakReference<ImageView>>() {
                        @Override
                        public void onImageDownloaded(Bitmap image, HttpUrl imageUrl, WeakReference<ImageView> callbackParams) {
                            mPhotoManager.getFullscreenCache().put(position, image);
                            ImageView view = callbackParams.get();
                            if (view != null) {
                                view.setImageBitmap(image);
                            }
                        }
                    };

            PhotoMeta.Copy fullscreenCopy = getFullscreenCopy(photoMeta);

            if (fullscreenCopy != null) {
                String urlString = fullscreenCopy.getUrl();
                HttpUrl url = HttpUrl.parse(urlString);
                ImageDownloadTask<WeakReference<ImageView>> imageDownloadTask = new ImageDownloadTask<>(
                        url,
                        handler,
                        new WeakReference<>(imageView)
                );
                imageDownloadTask.execute();
            } else {
                imageView.setImageResource(R.drawable.ic_broken_image_black_24dp);
            }
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

    private PhotoMeta.Copy getFullscreenCopy(PhotoMeta photoMeta) {
        //todo: вынести в константы (?)
        List<PhotoMeta.Copy.Type> fullscreenTypesByPriority = new ArrayList<>();
        fullscreenTypesByPriority.add(PhotoMeta.Copy.Type.PROPORTIONAL_1280);
        fullscreenTypesByPriority.add(PhotoMeta.Copy.Type.PROPORTIONAL_807);
        fullscreenTypesByPriority.add(PhotoMeta.Copy.Type.PROPORTIONAL_604);
        fullscreenTypesByPriority.add(PhotoMeta.Copy.Type.PROPORTIONAL_130);
        fullscreenTypesByPriority.add(PhotoMeta.Copy.Type.PROPORTIONAL_75);

        PhotoMeta.Copy copy = null;
        for (PhotoMeta.Copy.Type type : fullscreenTypesByPriority) {
            copy = photoMeta.getCopy(type);
            if (copy != null) break;
        }
        return copy;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
