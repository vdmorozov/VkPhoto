package com.example.morozovvd.vkphoto;

import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.morozovvd.vkphoto.objects.Photo;
import com.example.morozovvd.vkphoto.tasks.ImageDownloadTask;

import java.lang.ref.WeakReference;
import okhttp3.HttpUrl;

import static com.example.morozovvd.vkphoto.activities.FullscreenActivity.COPY_TYPE_FOR_FULLSCREEN;

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

        String urlString = photo.getCopy(COPY_TYPE_FOR_FULLSCREEN).getUrl();
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

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
