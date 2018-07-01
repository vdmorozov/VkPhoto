package com.example.morozovvd.vkphoto;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.example.morozovvd.vkphoto.objects.PhotoMeta;

import java.util.ArrayList;
import java.util.List;

public class PhotoManager {

    public static final int FULLSCREEN_CACHE_SIZE = 24;
    public static final int THUMBNAIL_CACHE_SIZE = 24 * 3;

    private static PhotoManager instance;

    private List<PhotoMeta> photoMetas;
    private LruCache<Integer, Bitmap> fullscreenCache;
    private LruCache<Integer, Bitmap> thumbnailCache;

    private PhotoManager() {
        photoMetas = new ArrayList<>();
        fullscreenCache = new LruCache<>(FULLSCREEN_CACHE_SIZE);
        thumbnailCache = new LruCache<>(THUMBNAIL_CACHE_SIZE);
    }

    public synchronized static PhotoManager getInstance() {
        if (instance == null) {
            instance = new PhotoManager();
        }
        return instance;
    }

    public List<PhotoMeta> getPhotoMetas() {
        return photoMetas;
    }

    public LruCache<Integer, Bitmap> getFullscreenCache() {
        return fullscreenCache;
    }

    public LruCache<Integer, Bitmap> getThumbnailCache() {
        return thumbnailCache;
    }
}
