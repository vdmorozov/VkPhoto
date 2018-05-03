package com.example.morozovvd.vkphoto.tasks;

import android.os.AsyncTask;

import com.example.morozovvd.vkphoto.commands.VkApiCommand;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class VkApiTask extends AsyncTask<Void, Void, Object> {

    private final VkApiCommand command;
    private final String commandId;
    private final WeakReference<ResponseHandler> handlerWeakRef;

    public VkApiTask(VkApiCommand command, String commandId, WeakReference<ResponseHandler> handlerWeakRef) {
        this.command = command;
        this.commandId = commandId;
        this.handlerWeakRef = handlerWeakRef;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        Object response = null;
        try {
            response = command.execute();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(Object response) {
        ResponseHandler handler = handlerWeakRef.get();
        if (handler != null && response != null) {
            handler.onVkApiTaskResponse(response, commandId);
        }
    }

    public interface ResponseHandler {
        void onVkApiTaskResponse(Object response, String commandId);
    }
}
