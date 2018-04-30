package com.example.morozovvd.vkphoto.commands;

import com.example.morozovvd.vkphoto.NetworkHelper;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class VkApiCommand<T> {

    public T execute() throws IOException, JSONException {
        OkHttpClient client = NetworkHelper.getHttpClient();

        HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .scheme("https")
                .host("api.vk.com")
                .addPathSegment("method")
                .addPathSegment(getMethodName())
                .addQueryParameter("access_token", NetworkHelper.getToken())
                .addQueryParameter("v", "5.74");

        for (Map.Entry<String, String> param: getParams().entrySet()) {
            urlBuilder.addQueryParameter(param.getKey(), param.getValue());
        }

        HttpUrl url = urlBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .build();

        ResponseBody responseBody = client.newCall(request).execute().body();
        if (responseBody == null) throw new IOException("empty response body");

        String jResponse = responseBody.string();
        return getParser().parse(jResponse);
    }

    protected abstract String getMethodName();

    protected abstract Map<String, String> getParams();

    protected abstract Parser<T> getParser();

    public interface Parser<T> {
        T parse(String jsonString) throws JSONException;
    }
}
