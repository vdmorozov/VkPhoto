package com.example.morozovvd.vkphoto.commands;

import com.example.morozovvd.vkphoto.parsers.PhotoListParser;

import java.util.HashMap;
import java.util.Map;

public class GetAllPhotosCommand extends VkApiCommand {

    private static final String KEY_OWNER_ID = "owner_id";
    private static final String KEY_COUNT = "count";
    private static final String KEY_OFFSET = "offset";
    private static final String KEY_NEED_EXTENDED_INFO = "extended";
    private static final String KEY_NEED_HIDDEN_FROM_PROFILE_PAGE_FLAG = "need_hidden";
    private static final String KEY_NEED_SPECIAL_SIZES = "photo_sizes";
    private static final String KEY_EXCLUDE_FROM_SERVICE_ALBUMS = "no_service_albums";
    private static final String KEY_EXCLUDE_HIDDEN_FROM_PROFILE_PAGE = "skip_hidden";

    private String ownerId;
    private int count;
    private int offset;
    private boolean needExtendedInfo;
    private boolean needHiddenFromProfilePageFlag;
    private boolean needSpecialSizes;
    private boolean excludeFromServiceAlbums;
    private boolean excludeHiddenFromProfilePage;

    public GetAllPhotosCommand(
            String ownerId,
            int count,
            int offset,
            boolean needExtendedInfo,
            boolean needHiddenFromProfilePageFlag,
            boolean needSpecialSizes,
            boolean excludeFromServiceAlbums,
            boolean excludeHiddenFromProfilePage) {
        this.ownerId = ownerId;
        this.count = count;
        this.offset = offset;
        this.needExtendedInfo = needExtendedInfo;
        this.needHiddenFromProfilePageFlag = needHiddenFromProfilePageFlag;
        this.needSpecialSizes = needSpecialSizes;
        this.excludeFromServiceAlbums = excludeFromServiceAlbums;
        this.excludeHiddenFromProfilePage = excludeHiddenFromProfilePage;
    }

    @Override
    protected String getMethodName() {
        return "photos.getAll";
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();

        if (ownerId != null) params.put(KEY_OWNER_ID, ownerId);
        params.put(KEY_COUNT, String.valueOf(count));
        params.put(KEY_OFFSET, String.valueOf(offset));
        params.put(KEY_NEED_EXTENDED_INFO, needExtendedInfo ? "1" : "0");
        params.put(KEY_NEED_HIDDEN_FROM_PROFILE_PAGE_FLAG, needHiddenFromProfilePageFlag ? "1" : "0");
        params.put(KEY_NEED_SPECIAL_SIZES, needSpecialSizes ? "1" : "0");
        params.put(KEY_EXCLUDE_FROM_SERVICE_ALBUMS, excludeFromServiceAlbums ? "1" : "0");
        params.put(KEY_EXCLUDE_HIDDEN_FROM_PROFILE_PAGE, excludeHiddenFromProfilePage ? "1" : "0");

        return params;
    }

    @Override
    protected Parser getParser() {
        return new PhotoListParser();
    }
}
