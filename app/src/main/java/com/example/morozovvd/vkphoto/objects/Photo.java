package com.example.morozovvd.vkphoto.objects;


import android.support.annotation.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Photo {

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

    public Photo(
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
    public int getWidth() {
        return width;
    }

    @Nullable
    public int getHeight() {
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

    public static class Copy {

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
            PROPORTIONAL_75 ("s"),
            PROPORTIONAL_130 ("m"),
            PROPORTIONAL_604 ("x"),
            PROPORTIONAL_807 ("y"),
            PROPORTIONAL_1280 ("z"),
            PROPORTIONAL_2560 ("w"),
            CUT_130 ("o"),
            CUT_200 ("p"),
            CUT_320 ("q"),
            CUT_510 ("r");

            private final String code;
            private final int maxWidth;
            private final int maxHeight;

            Type(String code) {
                this.code = code;
                switch (code) {
                    case "s":
                        maxWidth = maxHeight = 75;
                        break;
                    case "m":
                        maxWidth = maxHeight = 130;
                        break;
                    case "x":
                        maxWidth = maxHeight = 604;
                        break;
                    case "y":
                        maxWidth = maxHeight = 807;
                        break;
                    case "z":
                        maxWidth = 1280;
                        maxHeight = 1024;
                        break;
                    case "w":
                        maxWidth = 2560;
                        maxHeight = 2048;
                        break;
                    case "o":
                        maxWidth = maxHeight = 130;
                        break;
                    case "p":
                        maxWidth = maxHeight = 200;
                        break;
                    case "q":
                        maxWidth = maxHeight = 320;
                        break;
                    case "r":
                        maxWidth = maxHeight = 510;
                        break;
                    default:
                        maxWidth = maxHeight = 0;
                }
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
