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
import com.example.morozovvd.vkphoto.adapters.PhotoPagerAdapter;
import com.example.morozovvd.vkphoto.R;
import com.example.morozovvd.vkphoto.commands.GetMyPhotosCommand;
import com.example.morozovvd.vkphoto.objects.PhotoMetasResponse;
import com.example.morozovvd.vkphoto.tasks.VkApiTask;

import java.lang.ref.WeakReference;

public class FullscreenActivity extends AppCompatActivity implements VkApiTask.ResponseHandler {

    public static final String EXTRA_POSITION = "position";
    public static final String FETCH_NEXT_PAGE = "FETCH_NEXT_PAGE";

    public static final int INITIAL_HIDE_DELAY = 100;
    private static final int PAGE_SIZE = 15;

    private boolean mVisible;
    private ViewPager mViewPager;
    private PhotoPagerAdapter mPhotoPagerAdapter;

    private boolean loadingInProgress = false;
    private boolean loadedAll = false;

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

        mPhotoPagerAdapter = new PhotoPagerAdapter(PhotoManager.getInstance().getFullscreenCache());
        mPhotoPagerAdapter.addPhotoMetas(PhotoManager.getInstance().getPhotoMetas());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //todo: подгрузка заранее
                if (position == mPhotoPagerAdapter.getCount() - 1) {
                    onScrolledToLast();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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

    private void onScrolledToLast() {
        load(PAGE_SIZE, mPhotoPagerAdapter.getCount());
    }

    private void load(int count, int offset) {
        if (loadingInProgress || loadedAll) return;

        loadingInProgress = true;
        //для увеличения производительности можно сделать команды мутабельными и
        //переиспользовать команду, меняя count и offset
        GetMyPhotosCommand command = new GetMyPhotosCommand(
                count,
                offset,
                false,
                false,
                true,
                false,
                false
        );

        VkApiTask getPhotoListTask = new VkApiTask(
                command,
                FETCH_NEXT_PAGE,
                new WeakReference<>((VkApiTask.ResponseHandler) this)
        );
        getPhotoListTask.execute();
    }

    @Override
    public void onVkApiTaskResponse(Object response, String commandId) {
        switch (commandId) {
            case FETCH_NEXT_PAGE:
                loadingInProgress = false;
                PhotoMetasResponse photoMetasResponse = (PhotoMetasResponse) response;
                loadedAll = photoMetasResponse.getPhotoMetas().isEmpty();
                if (loadedAll) break;
                mPhotoPagerAdapter.addPhotoMetas(photoMetasResponse.getPhotoMetas());
                break;
            default:
                //do nothing
        }
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
