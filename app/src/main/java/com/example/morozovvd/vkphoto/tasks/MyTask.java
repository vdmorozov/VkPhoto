package com.example.morozovvd.vkphoto.tasks;

import android.os.AsyncTask;

import com.example.morozovvd.vkphoto.activities.MainActivity;
import com.example.morozovvd.vkphoto.commands.GetMyPhotosCommand;
import com.example.morozovvd.vkphoto.objects.PhotoResponse;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class MyTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<MainActivity> weakMainActivity;

    public MyTask(WeakReference<MainActivity> mainActivity) {
        this.weakMainActivity = mainActivity;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        GetMyPhotosCommand command = new GetMyPhotosCommand(
                1,
                0,
                false,
                false,
                false,
                true,
                true
        );

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            PhotoResponse response = command.execute();
        } catch (IOException | JSONException e) {
            //todo: show error to user
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void o) {
        MainActivity strongMainActivity = weakMainActivity.get();
        if (strongMainActivity != null) {
            strongMainActivity.showToast();
        }
    }
}
