package com.example.morozovvd.vkphoto.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.example.morozovvd.vkphoto.R;
import com.example.morozovvd.vkphoto.objects.Photo;
import com.example.morozovvd.vkphoto.tasks.ImageDownloadTask;

import java.lang.ref.WeakReference;

import okhttp3.HttpUrl;

public class FullscreenActivity extends AppCompatActivity implements ImageDownloadTask.ResponseHandler {

    public static final String EXTRA_PHOTO = "photo";
    public static final int INITIAL_HIDE_DELAY = 100;
    public static final Photo.Copy.Type COPY_TYPE_FOR_FULLSCREEN = Photo.Copy.Type.PROPORTIONAL_1280;

    private ImageView mImageView;
    private boolean mVisible;
    private Photo mPhoto;

    static public Intent getCallingIntent(Context context, Photo photo) {
        final Intent intent = new Intent(context, FullscreenActivity.class);
        intent.putExtra(EXTRA_PHOTO, photo);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mImageView = findViewById(R.id.fullscreen_photo);

        mPhoto = (Photo) getIntent().getSerializableExtra(EXTRA_PHOTO);

        String urlString = mPhoto.getCopy(COPY_TYPE_FOR_FULLSCREEN).getUrl();
        HttpUrl url = HttpUrl.parse(urlString);
        ImageDownloadTask imageDownloadTask = new ImageDownloadTask(
                url,
                0,
                new WeakReference<>((ImageDownloadTask.ResponseHandler) this)
        );

        imageDownloadTask.execute();

        // Set up the user interaction to manually show or hide the system UI.
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(INITIAL_HIDE_DELAY);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    @SuppressLint("InlinedApi")
    private void hide() {
        mVisible = false;

        mImageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mImageView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;
    }

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onImageDownloaded(Bitmap image, int imageId, HttpUrl imageUrl) {
        mImageView.setImageBitmap(image);
    }
}
