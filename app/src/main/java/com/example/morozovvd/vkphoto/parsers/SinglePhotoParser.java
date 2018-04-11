package com.example.morozovvd.vkphoto.parsers;

import com.example.morozovvd.vkphoto.commands.VkApiCommand;
import com.example.morozovvd.vkphoto.objects.Photo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SinglePhotoParser implements VkApiCommand.Parser<Photo> {

    @Override
    public Photo parse(String jsonString) throws JSONException {
        final JSONObject obj = new JSONObject(jsonString);
        final int id = obj.getInt("id");
        final int albumId = obj.getInt("album_id");
        final int ownerId = obj.getInt("owner_id");
        final String text = obj.getString("text");
        final int timestamp = obj.getInt("date");
        final Date date = new Date((long)timestamp*1000);

        Integer width = obj.optInt("width");
        Integer height = obj.optInt("height");
        if (width == 0) width = null;
        if (height == 0) height = null;

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

        return parseCopies(obj, photo);
    }

    private Photo parseCopies(JSONObject jPhoto, Photo photo) throws JSONException {
        final JSONArray sizes = jPhoto.optJSONArray("sizes");
        if (sizes != null) {
            for (int i = 0; i < sizes.length(); i++) {
                JSONObject jSize = sizes.getJSONObject(i);
                Photo.Copy photoCopy = parseSizeItem(jSize);
                photo.addCopy(photoCopy);
            }
        } else {
            Map<String, Photo.Copy.Type> keyToTypeMatching = new HashMap<>();
            keyToTypeMatching.put("photo_75", Photo.Copy.Type.PROPORTIONAL_75);
            keyToTypeMatching.put("photo_130", Photo.Copy.Type.PROPORTIONAL_130);
            keyToTypeMatching.put("photo_604", Photo.Copy.Type.PROPORTIONAL_604);
            keyToTypeMatching.put("photo_807", Photo.Copy.Type.PROPORTIONAL_807);
            keyToTypeMatching.put("photo_1280", Photo.Copy.Type.PROPORTIONAL_1280);
            keyToTypeMatching.put("photo_2560", Photo.Copy.Type.PROPORTIONAL_2560);


            for (Map.Entry<String, Photo.Copy.Type> keyTypePair : keyToTypeMatching.entrySet()) {
                String copyUrl = jPhoto.optString(keyTypePair.getKey());
                if (copyUrl.equals("")) continue;
                final Photo.Copy photoCopy = new Photo.Copy(keyTypePair.getValue(), copyUrl, null, null);
                photo.addCopy(photoCopy);
            }
        }

        return photo;
    }

    private Photo.Copy parseSizeItem(JSONObject jCopy) throws JSONException{
        final String src = jCopy.getString("src");
        final String typeCode = jCopy.getString("type");
        Integer width = jCopy.getInt("width");
        Integer height = jCopy.getInt("height");
        if (width == 0) width = null;
        if (height == 0) height = null;

        final Photo.Copy.Type type;
        switch (typeCode) {
            case "s":
                type = Photo.Copy.Type.PROPORTIONAL_75;
                break;
            case "m":
                type = Photo.Copy.Type.PROPORTIONAL_130;
                break;
            case "x":
                type = Photo.Copy.Type.PROPORTIONAL_604;
                break;
            case "y":
                type = Photo.Copy.Type.PROPORTIONAL_807;
                break;
            case "z":
                type = Photo.Copy.Type.PROPORTIONAL_1280;
                break;
            case "w":
                type = Photo.Copy.Type.PROPORTIONAL_2560;
                break;
            case "o":
                type = Photo.Copy.Type.CUT_130;
                break;
            case "p":
                type = Photo.Copy.Type.CUT_200;
                break;
            case "q":
                type = Photo.Copy.Type.CUT_320;
                break;
            case "r":
                type = Photo.Copy.Type.CUT_510;
                break;
            default:
                type = Photo.Copy.Type.UNKNOWN;
        }
        return new Photo.Copy(type, src, width, height);
    }
}
