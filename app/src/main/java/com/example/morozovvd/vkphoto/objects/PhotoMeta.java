package com.example.morozovvd.vkphoto.objects;


import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PhotoMeta implements Serializable {

    private final int id;
    private final int albumId;
    private final int ownerId;
    private final Date date;
    private final String text;

    @Nullable
    private final Integer width;

    @Nullable
    private final Integer height;

    @Nullable
    private final Integer repostsCount;

    @Nullable
    private final Integer likesCount;

    @Nullable
    private final Boolean currentUserLikes;

    private final Map<Copy.Type, Copy> copies;

    public PhotoMeta(
            int id,
            int albumId,
            int ownerId,
            Date date,
            String text,
            @Nullable Integer width,
            @Nullable Integer height,
            @Nullable Integer repostsCount,
            @Nullable Integer likesCount,
            @Nullable Boolean currentUserLikes) {
        this.id = id;
        this.albumId = albumId;
        this.ownerId = ownerId;
        this.width = width;
        this.height = height;
        this.date = date;
        this.text = text;
        this.repostsCount = repostsCount;
        this.likesCount = likesCount;
        this.currentUserLikes = currentUserLikes;
        copies = new HashMap<>();
    }

    public void addCopy(Copy copy) {
        copies.put(copy.getType(), copy);
    }

    public Copy getCopy(Copy.Type copyType) {
        return copies.get(copyType);
    }

    public int getId() {
        return id;
    }

    public int getAlbumId() {
        return albumId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public Date getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    @Nullable
    public Integer getWidth() {
        return width;
    }

    @Nullable
    public Integer getHeight() {
        return height;
    }

    @Nullable
    public Integer getRepostsCount() {
        return repostsCount;
    }

    @Nullable
    public Integer getLikesCount() {
        return likesCount;
    }

    @Nullable
    public Boolean getCurrentUserLikes() {
        return currentUserLikes;
    }

    public static class Copy implements Serializable {

        private final Type type;
        private final String url;

        @Nullable
        private final Integer width;

        @Nullable
        private final Integer height;

        public Copy(Type type, String url, @Nullable Integer width, @Nullable Integer height) {
            this.type = type;
            this.url = url;
            this.width = width;
            this.height = height;
        }

        public Type getType() {
            return type;
        }

        public String getUrl() {
            return url;
        }

        @Nullable
        public Integer getWidth() {
            return width;
        }

        @Nullable
        public Integer getHeight() {
            return height;
        }

        public enum Type {
            PROPORTIONAL_75("s", 75, 75),
            PROPORTIONAL_130("m", 130, 130),
            PROPORTIONAL_604("x", 604, 604),
            PROPORTIONAL_807("y", 807, 807),
            PROPORTIONAL_1280("z", 1280, 1024),
            PROPORTIONAL_2560("w", 2560, 2048),
            CUT_130("o", 130, 130),
            CUT_200("p", 200, 200),
            CUT_320("q", 320, 320),
            CUT_510("r", 510, 510),
            UNKNOWN("", 0, 0);

            private final String code;
            private final int maxWidth;
            private final int maxHeight;

            Type(String code, int maxWidth, int maxHeight) {
                this.code = code;
                this.maxWidth = maxWidth;
                this.maxHeight = maxHeight;
            }

            public String getCode() {
                return code;
            }

            public int getMaxWidth() {
                return maxWidth;
            }

            public int getMaxHeight() {
                return maxHeight;
            }
        }
    }
}
