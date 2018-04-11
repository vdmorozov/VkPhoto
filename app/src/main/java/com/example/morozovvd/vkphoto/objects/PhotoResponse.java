package com.example.morozovvd.vkphoto.objects;

import java.util.List;

public class PhotoResponse {
    private final int totalCount;
    private final List<Photo> list;
    private final boolean hasNext;

    public PhotoResponse(int totalCount, List<Photo> list, boolean hasNext) {
        this.totalCount = totalCount;
        this.list = list;
        this.hasNext = hasNext;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<Photo> getList() {
        return list;
    }

    public boolean hasNext() {
        return hasNext;
    }
}
