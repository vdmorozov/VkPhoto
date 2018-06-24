package com.example.morozovvd.vkphoto.parsers;

import com.example.morozovvd.vkphoto.objects.PhotoMeta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SinglePhotoMetaParser {

    public PhotoMeta parse(JSONObject jPhoto) throws JSONException {
        final int id = jPhoto.getInt("id");
        final int albumId = jPhoto.getInt("album_id");
        final int ownerId = jPhoto.getInt("owner_id");
        final String text = jPhoto.getString("text");
        final int timestamp = jPhoto.getInt("date");
        final Date date = new Date((long)timestamp*1000);

        Integer width = jPhoto.optInt("width");
        Integer height = jPhoto.optInt("height");
        if (width == 0) width = null;
        if (height == 0) height = null;

        Boolean currentUserLikes = null;
        Integer likesCount = null;
        Integer repostsCount = null;

        final JSONObject likes = jPhoto.optJSONObject("likes");
        if (likes != null) {
            currentUserLikes = likes.getInt("user_likes") == 1;
            likesCount = likes.getInt("count");
        }

        final JSONObject reposts = jPhoto.optJSONObject("reposts");
        if (reposts != null) {
            repostsCount = reposts.getInt("count");
        }

        PhotoMeta photoMeta = new PhotoMeta(
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

        return parseAndAddCopies(jPhoto, photoMeta);
    }

    private PhotoMeta parseAndAddCopies(JSONObject jPhoto, PhotoMeta photoMeta) throws JSONException {
        final JSONArray sizes = jPhoto.optJSONArray("sizes");
        if (sizes != null) {
            for (int i = 0; i < sizes.length(); i++) {
                JSONObject jSize = sizes.getJSONObject(i);
                PhotoMeta.Copy photoCopy = parseSizeItem(jSize);
                photoMeta.addCopy(photoCopy);
            }
        } else {
            Map<String, PhotoMeta.Copy.Type> keyToTypeMatching = new HashMap<>();
            keyToTypeMatching.put("photo_75", PhotoMeta.Copy.Type.PROPORTIONAL_75);
            keyToTypeMatching.put("photo_130", PhotoMeta.Copy.Type.PROPORTIONAL_130);
            keyToTypeMatching.put("photo_604", PhotoMeta.Copy.Type.PROPORTIONAL_604);
            keyToTypeMatching.put("photo_807", PhotoMeta.Copy.Type.PROPORTIONAL_807);
            keyToTypeMatching.put("photo_1280", PhotoMeta.Copy.Type.PROPORTIONAL_1280);
            keyToTypeMatching.put("photo_2560", PhotoMeta.Copy.Type.PROPORTIONAL_2560);


            for (Map.Entry<String, PhotoMeta.Copy.Type> keyTypePair : keyToTypeMatching.entrySet()) {
                String copyUrl = jPhoto.optString(keyTypePair.getKey());
                if (copyUrl.equals("")) continue;
                final PhotoMeta.Copy photoCopy = new PhotoMeta.Copy(keyTypePair.getValue(), copyUrl, null, null);
                photoMeta.addCopy(photoCopy);
            }
        }

        return photoMeta;
    }

    private PhotoMeta.Copy parseSizeItem(JSONObject jCopy) throws JSONException{
        final String src = jCopy.getString("src");
        final String typeCode = jCopy.getString("type");
        Integer width = jCopy.getInt("width");
        Integer height = jCopy.getInt("height");
        if (width == 0) width = null;
        if (height == 0) height = null;

        final PhotoMeta.Copy.Type type;
        switch (typeCode) {
            case "s":
                type = PhotoMeta.Copy.Type.PROPORTIONAL_75;
                break;
            case "m":
                type = PhotoMeta.Copy.Type.PROPORTIONAL_130;
                break;
            case "x":
                type = PhotoMeta.Copy.Type.PROPORTIONAL_604;
                break;
            case "y":
                type = PhotoMeta.Copy.Type.PROPORTIONAL_807;
                break;
            case "z":
                type = PhotoMeta.Copy.Type.PROPORTIONAL_1280;
                break;
            case "w":
                type = PhotoMeta.Copy.Type.PROPORTIONAL_2560;
                break;
            case "o":
                type = PhotoMeta.Copy.Type.CUT_130;
                break;
            case "p":
                type = PhotoMeta.Copy.Type.CUT_200;
                break;
            case "q":
                type = PhotoMeta.Copy.Type.CUT_320;
                break;
            case "r":
                type = PhotoMeta.Copy.Type.CUT_510;
                break;
            default:
                type = PhotoMeta.Copy.Type.UNKNOWN;
        }
        return new PhotoMeta.Copy(type, src, width, height);
    }
}
