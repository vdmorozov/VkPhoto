package com.example.morozovvd.vkphoto;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.example.morozovvd.vkphoto.commands.GetMyPhotosCommand;
import com.example.morozovvd.vkphoto.objects.Photo;
import com.example.morozovvd.vkphoto.objects.PhotoResponse;
import com.example.morozovvd.vkphoto.tasks.VkApiTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PhotoManager implements VkApiTask.ResponseHandler {

    public static final int PAGE_SIZE = 24;
    public static final String FETCH_NEXT_PAGE = "FETCH_NEXT_PAGE";

    private static PhotoManager instance;

    private int page = -1;
    private boolean loadingInProgress = false;
    private boolean loadedAll = false;
    private List<Photo> photos;
    private DataSetObservable observable;
    private LruCache<Integer, Bitmap> fullscreenCache;
    private LruCache<Integer, Bitmap> thumbnailCache;

    private PhotoManager() {
        photos = new ArrayList<>();
        observable = new DataSetObservable();
        fullscreenCache = new LruCache<>(PAGE_SIZE);
        thumbnailCache = new LruCache<>(PAGE_SIZE * 3);
    }

    public synchronized static PhotoManager getInstance() {
        if (instance == null) {
            instance = new PhotoManager();
        }
        return instance;
    }

    public int getCount() {
        return photos.size();
    }

    public Photo get(int position) {
        return photos.get(position);
    }

    public LruCache<Integer, Bitmap> getFullscreenCache() {
        return fullscreenCache;
    }

    public LruCache<Integer, Bitmap> getThumbnailCache() {
        return thumbnailCache;
    }

    public void registerObserver(DataSetObserver observer) {
        observable.registerObserver(observer);
    }

    public void fetchNextPage() {
        if (loadingInProgress || loadedAll) return;

        loadingInProgress = true;
        page++;
        //для увеличения производительности можно сделать команды мутабельными и
        //переиспользовать команду, меняя count и offset
        GetMyPhotosCommand command = new GetMyPhotosCommand(
                PAGE_SIZE,
                PAGE_SIZE * page,
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
                PhotoResponse photoResponse = (PhotoResponse) response;
                loadedAll = photoResponse.getList().isEmpty();
                if (loadedAll) break;
                photos.addAll(photoResponse.getList());
                observable.notifyChanged();
                break;
            default:
                //do nothing
        }
    }
}
