package com.example.morozovvd.vkphoto;

import okhttp3.OkHttpClient;

public class NetworkHelper {

    private static String token;
    private static OkHttpClient httpClient;

    public static void setToken(String token) {
        NetworkHelper.token = token;
    }

    public static String getToken() {
        return token;
    }

    public static OkHttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new OkHttpClient();
        }
        return httpClient;
    }
}
