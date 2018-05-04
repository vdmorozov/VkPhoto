package com.example.morozovvd.vkphoto.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.morozovvd.vkphoto.NetworkHelper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ImageDownloadTask extends AsyncTask<Void, Void, Bitmap> {

    private HttpUrl imageUrl;
    private int imageId;
    private WeakReference<ResponseHandler> handlerWeakRef;

    public ImageDownloadTask(HttpUrl imageUrl, int imageId, WeakReference<ResponseHandler> handlerWeakRef) {
        this.imageUrl = imageUrl;
        this.imageId = imageId;
        this.handlerWeakRef = handlerWeakRef;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Request request = new Request.Builder().url(imageUrl).build();
        OkHttpClient client = NetworkHelper.getHttpClient();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response == null) return null;
        ResponseBody body = response.body();

        if (body == null) return null;
        InputStream inputStream = body.byteStream();

        return BitmapFactory.decodeStream(inputStream);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ResponseHandler handler = handlerWeakRef.get();
        if (handler != null && bitmap != null) {
            handler.onImageDownloaded(bitmap, imageId, imageUrl);
        }
    }

    public interface ResponseHandler {
        //todo: стоит ли передавать урл?
        void onImageDownloaded(Bitmap image, int imageId, HttpUrl imageUrl);
    }
}
