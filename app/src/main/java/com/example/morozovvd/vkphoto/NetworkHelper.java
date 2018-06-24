package com.example.morozovvd.vkphoto;

import okhttp3.OkHttpClient;

public class NetworkHelper {

    private static NetworkHelper instance;

    private String token;
    private OkHttpClient httpClient;

    private NetworkHelper() {
    }

    public synchronized static NetworkHelper getInstance() {
        if (instance == null) {
            instance = new NetworkHelper();
        }
        return instance;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public OkHttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new OkHttpClient();
        }
        return httpClient;
    }
}
