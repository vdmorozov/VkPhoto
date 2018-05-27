package com.example.morozovvd.vkphoto.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.morozovvd.vkphoto.PhotoManager;
import com.example.morozovvd.vkphoto.PhotoPagerAdapter;
import com.example.morozovvd.vkphoto.R;
import com.example.morozovvd.vkphoto.objects.Photo;

public class FullscreenActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "position";

    public static final int INITIAL_HIDE_DELAY = 100;
    public static final Photo.Copy.Type COPY_TYPE_FOR_FULLSCREEN = Photo.Copy.Type.PROPORTIONAL_1280;

    private boolean mVisible;
    private ViewPager mViewPager;
    private PhotoPagerAdapter mPhotoPagerAdapter;


    static public Intent getCallingIntent(Context context, int position) {
        Intent callingIntent = new Intent(context, FullscreenActivity.class);
        callingIntent.putExtra(EXTRA_POSITION, position);
        return callingIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        int position = getIntent().getIntExtra(EXTRA_POSITION, 0);

        mViewPager = findViewById(R.id.photo_pager);

        mPhotoPagerAdapter = new PhotoPagerAdapter(PhotoManager.getInstance());
        mViewPager.setAdapter(mPhotoPagerAdapter);
        mViewPager.setCurrentItem(position);

        mVisible = true;

        // Set up the user interaction to manually show or hide the system UI.
        mPhotoPagerAdapter.setOnItemClickListener(new PhotoPagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
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

        mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
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
}
