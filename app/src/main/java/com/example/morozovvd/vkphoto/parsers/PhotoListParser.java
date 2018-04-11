package com.example.morozovvd.vkphoto.parsers;

import com.example.morozovvd.vkphoto.commands.VkApiCommand;
import com.example.morozovvd.vkphoto.objects.Photo;
import com.example.morozovvd.vkphoto.objects.PhotoResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PhotoListParser implements VkApiCommand.Parser<PhotoResponse> {

    @Override
    public PhotoResponse parse(String jsonString) throws JSONException {
        final JSONObject root = new JSONObject(jsonString);
        final JSONObject response = root.getJSONObject("response");
        final int totalCount = response.getInt("count");
        final boolean hasNext = response.getInt("more") == 1;

        final JSONArray jPhotos = response.getJSONArray("items");

        List<Photo> photos = new ArrayList<>();
        SinglePhotoParser singlePhotoParser = new SinglePhotoParser();
        for (int i = 0; i < jPhotos.length(); i++) {
            final Photo photo = singlePhotoParser.parse(jPhotos.getJSONObject(i));
            photos.add(photo);
        }

        return new PhotoResponse(totalCount, photos, hasNext);
    }
}
