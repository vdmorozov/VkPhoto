package com.example.morozovvd.vkphoto.objects;

import java.util.List;

public class PhotoMetasResponse {
    private final int totalCount;
    private final List<PhotoMeta> photoMetas;
    private final boolean hasNext;

    public PhotoMetasResponse(int totalCount, List<PhotoMeta> photoMetas, boolean hasNext) {
        this.totalCount = totalCount;
        this.photoMetas = photoMetas;
        this.hasNext = hasNext;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<PhotoMeta> getPhotoMetas() {
        return photoMetas;
    }

    public boolean hasNext() {
        return hasNext;
    }
}
