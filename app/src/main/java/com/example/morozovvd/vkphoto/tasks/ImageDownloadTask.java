package com.example.morozovvd.vkphoto.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.morozovvd.vkphoto.NetworkHelper;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

//todo: не слишком ли универсально, может сразу хранить ссылку на ImageView?
public class ImageDownloadTask<CallbackParamsT> extends AsyncTask<Void, Void, Bitmap> {

    private HttpUrl imageUrl;
    private ResponseHandler<CallbackParamsT> responseHandler;
    private CallbackParamsT callbackParams;

    public ImageDownloadTask(HttpUrl imageUrl, ResponseHandler<CallbackParamsT> handler, CallbackParamsT callbackParams) {
        this.imageUrl = imageUrl;
        this.callbackParams = callbackParams;
        this.responseHandler = handler;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        // точно ли нужно выносить такие зависимости? запрос от либы OkHttp,
        // хэлпер аналогично получается из синглтона в БО и не вынесен в параметры
        Request request = new Request.Builder().url(imageUrl).build();
        OkHttpClient client = NetworkHelper.getInstance().getHttpClient();

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
        if (responseHandler != null && bitmap != null) {
            responseHandler.onImageDownloaded(bitmap, imageUrl, callbackParams);
        }
    }

    public interface ResponseHandler<T> {
        //todo: стоит ли передавать урл?
        void onImageDownloaded(Bitmap image, HttpUrl imageUrl, T callbackParams);
    }
}
