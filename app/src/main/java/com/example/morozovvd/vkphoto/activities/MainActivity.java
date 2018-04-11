package com.example.morozovvd.vkphoto.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.morozovvd.vkphoto.commands.GetMyPhotosCommand;
import com.example.morozovvd.vkphoto.NetworkHelper;
import com.example.morozovvd.vkphoto.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    static final int AUTH_REQUEST = 1;

    private ImageView myImageView;
    private Context mContext;
    private Handler mPhotosCallbackHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        myImageView = findViewById(R.id.myImageView);

        mPhotosCallbackHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String messageToShow = (String) msg.obj;
                Toast.makeText(mContext, messageToShow, Toast.LENGTH_SHORT).show();
            }
        };

        //todo: отслеживание состояния авторизации
        boolean authorized = (NetworkHelper.getToken() != null);

        if (!authorized) {
            Intent oauthActivityIntent = new Intent(this, OauthActivity.class);
            startActivityForResult(oauthActivityIntent, AUTH_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTH_REQUEST && resultCode == RESULT_OK) {
            NetworkHelper.setToken(data.getStringExtra(OauthActivity.EXTRA_TOKEN));
            testVkApi();
        }
    }

    private void testVkApi() {

        GetMyPhotosCommand command = new GetMyPhotosCommand(
                1,
                0,
                false,
                false,
                false,
                true,
                true
        );

        command.execute(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mPhotosCallbackHandler.obtainMessage(0,"FAIL").sendToTarget();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mPhotosCallbackHandler.obtainMessage(0, "S U C C E S S").sendToTarget();
                Log.d("VK_API_TEST", response.body().string());
            }
        });
    }
}
