package com.example.morozovvd.vkphoto.commands;

import com.example.morozovvd.vkphoto.NetworkHelper;

import org.json.JSONException;

import java.util.Map;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public abstract class VkApiCommand {

    public void execute(Callback callback) {
        OkHttpClient client = NetworkHelper.getHttpClient();

        HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .scheme("https")
                .host("api.vk.com")
                .addPathSegment("method")
                .addPathSegment(getMethodName())
                .addQueryParameter("access_token", NetworkHelper.getToken())
                .addQueryParameter("v", "5.73");

        for (Map.Entry<String, String> param: getParams().entrySet()) {
            urlBuilder.addQueryParameter(param.getKey(), param.getValue());
        }

        HttpUrl url = urlBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }

    protected abstract String getMethodName();

    protected abstract Map<String, String> getParams();

    protected abstract Parser getParser();

    public interface Parser<T> {
        T parse(String jsonString) throws JSONException;
    }
}
