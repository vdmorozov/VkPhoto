package com.example.morozovvd.vkphoto.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.morozovvd.vkphoto.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OauthActivity extends AppCompatActivity {

    private static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
    public static final String EXTRA_TOKEN = "token";
    public static final int APP_ID = 6376021;
    public static final String API_VERSION = "5.73";
    public static final String PARAMS_TOKEN_KEY = "access_token";

    protected WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mWebView = findViewById(R.id.oauthWebView);

        loadPage();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void loadPage() {
        try {
            //todo: вынести в константы/параметры
            String scope = "photos";
            boolean revoke = true;

            String urlToLoad = String.format(Locale.US,
                    "https://oauth.vk.com/authorize?client_id=%s" +
                            "&scope=%s" +
                            "&redirect_uri=%s" +
                            "&display=mobile" +
                            "&v=%s" +
                            "&response_type=token&revoke=%d",
                    APP_ID, scope, REDIRECT_URL, API_VERSION, revoke ? 1 : 0);

            mWebView.setWebViewClient(new OAuthWebViewClient(this));
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.loadUrl(urlToLoad);
            mWebView.setBackgroundColor(Color.TRANSPARENT);
            mWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            mWebView.setVerticalScrollBarEnabled(false);
            mWebView.setVisibility(View.VISIBLE);
            mWebView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
            //mProgress.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    private static class OAuthWebViewClient extends WebViewClient {
        @NonNull
        final OauthActivity oauthActivity;

        public OAuthWebViewClient(@NonNull OauthActivity oauthActivity) {
            this.oauthActivity = oauthActivity;
        }

        boolean processUrl(String url) {
            if (url.startsWith(REDIRECT_URL)) {
                Intent intent = new Intent();
                String urlFragment = url.substring(url.indexOf('#') + 1);
                Map<String, String> params = explodeQueryString(urlFragment);

                if (params == null) {
                    oauthActivity.setResult(RESULT_CANCELED, intent);
                    oauthActivity.finish();
                    return true;
                }

                String token = params.get(PARAMS_TOKEN_KEY);

                if (token == null) {
                    oauthActivity.setResult(RESULT_CANCELED, intent);
                    oauthActivity.finish();
                    return true;
                }

                intent.putExtra(EXTRA_TOKEN, token);
                oauthActivity.setResult(RESULT_OK, intent);
                oauthActivity.finish();

                return true;
            }
            return false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return processUrl(url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            processUrl(url);
        }

        //todo: вынести
        /**
         * Breaks key=value&key=value string to map
         *
         * @param queryString string to explode
         * @return Key-value map of passed string
         */
        @Nullable
        public static Map<String, String> explodeQueryString(@Nullable String queryString) {
            if (queryString == null) {
                return null;
            }
            String[] keyValuePairs = queryString.split("&");
            HashMap<String, String> parameters = new HashMap<>(keyValuePairs.length);

            for (String keyValueString : keyValuePairs) {
                String[] keyValueArray = keyValueString.split("=");
                parameters.put(keyValueArray[0], keyValueArray[1]);
            }
            return parameters;
        }
    }
}
