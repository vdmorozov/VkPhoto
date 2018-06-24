package com.example.morozovvd.vkphoto.parsers;

import com.example.morozovvd.vkphoto.commands.VkApiCommand;
import com.example.morozovvd.vkphoto.objects.PhotoMeta;
import com.example.morozovvd.vkphoto.objects.PhotoMetasResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PhotoMetasParser implements VkApiCommand.Parser<PhotoMetasResponse> {

    @Override
    public PhotoMetasResponse parse(String jsonString) throws JSONException {
        final JSONObject root = new JSONObject(jsonString);
        final JSONObject response = root.getJSONObject("response");
        final int totalCount = response.getInt("count");
        final boolean hasNext = response.optInt("more") == 1;

        final JSONArray jPhotos = response.getJSONArray("items");

        List<PhotoMeta> photoMetas = new ArrayList<>();
        SinglePhotoMetaParser singlePhotoMetaParser = new SinglePhotoMetaParser();
        for (int i = 0; i < jPhotos.length(); i++) {
            final PhotoMeta photoMeta = singlePhotoMetaParser.parse(jPhotos.getJSONObject(i));
            photoMetas.add(photoMeta);
        }

        return new PhotoMetasResponse(totalCount, photoMetas, hasNext);
    }
}
