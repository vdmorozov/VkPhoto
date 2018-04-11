package com.example.morozovvd.vkphoto.parsers;

import com.example.morozovvd.vkphoto.commands.VkApiCommand;
import com.example.morozovvd.vkphoto.objects.Photo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class SinglePhotoParser implements VkApiCommand.Parser<Photo> {

    @Override
    public Photo parse(String jsonString) throws JSONException {
        final JSONObject obj = new JSONObject(jsonString);
        final int id = obj.getInt("id");
        final int albumId = obj.getInt("album_id");
        final int ownerId = obj.getInt("owner_id");
        final int width = obj.getInt("width");
        final int height = obj.getInt("height");
        final String text = obj.getString("text");
        final int timestamp = obj.getInt("date");
        final Date date = new Date((long)timestamp*1000);

        Boolean currentUserLikes = null;
        Integer likesCount = null;
        Integer repostsCount = null;

        final JSONObject likes = obj.optJSONObject("likes");
        if (likes != null) {
            currentUserLikes = likes.getInt("user_likes") == 1;
            likesCount = likes.getInt("count");
        }

        final JSONObject reposts = obj.optJSONObject("reposts");
        if (reposts != null) {
            repostsCount = reposts.getInt("count");
        }

        Photo photo = new Photo(
                id,
                albumId,
                ownerId,
                date,
                text,
                width,
                height,
                repostsCount,
                likesCount,
                currentUserLikes
        );

        final JSONArray sizes = obj.optJSONArray("sizes");
        if (sizes != null) {
            for (int i = 0; i < sizes.length(); i++) {
                JSONObject jSize = sizes.getJSONObject(i);
                Photo.Copy photoCopy = parseCopy(jSize);
                photo.addCopy(photoCopy);
            }
        } else {
            //todo: спарсить урлы из ключей photo_75 и т.д.
        }

        return photo;
    }

    private Photo.Copy parseCopy(JSONObject jCopy) throws JSONException{
        final String src = jCopy.getString("src");
        final String type = jCopy.getString("type");
        Integer width = jCopy.getInt("width");
        Integer height = jCopy.getInt("height");
        if (width == 0) width = null;
        if (height == 0) height = null;
        return new Photo.Copy(Photo.Copy.Type.valueOf(type), src, width, height);
    }
}
