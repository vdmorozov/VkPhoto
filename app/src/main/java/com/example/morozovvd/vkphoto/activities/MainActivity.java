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

import com.example.morozovvd.vkphoto.NetworkHelper;
import com.example.morozovvd.vkphoto.R;
import com.example.morozovvd.vkphoto.objects.Photo;
import com.example.morozovvd.vkphoto.parsers.SinglePhotoParser;

import org.json.JSONException;

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

        try {
            Photo testPhoto = (new SinglePhotoParser()).parse("{\"id\":456240187,\"album_id\":-7,\"owner_id\":214437822,\"sizes\":[{\"src\":\"https://pp.userap...1a8/-8Gzgbmzj64.jpg\",\"width\":130,\"height\":97,\"type\":\"m\"},{\"src\":\"https://pp.userap...1ad/RUaefenLP6U.jpg\",\"width\":130,\"height\":98,\"type\":\"o\"},{\"src\":\"https://pp.userap...1ae/WywBls5V9Lc.jpg\",\"width\":200,\"height\":150,\"type\":\"p\"},{\"src\":\"https://pp.userap...1af/TVkhpi54-nM.jpg\",\"width\":320,\"height\":240,\"type\":\"q\"},{\"src\":\"https://pp.userap...1b0/KSdzEQ4y9GU.jpg\",\"width\":510,\"height\":383,\"type\":\"r\"},{\"src\":\"https://pp.userap...1a7/X2olfQTd0-o.jpg\",\"width\":75,\"height\":56,\"type\":\"s\"},{\"src\":\"https://pp.userap...1ac/Y2QkeswSw2M.jpg\",\"width\":2560,\"height\":1920,\"type\":\"w\"},{\"src\":\"https://pp.userap...1a9/du89GvLYPRM.jpg\",\"width\":604,\"height\":453,\"type\":\"x\"},{\"src\":\"https://pp.userap...1aa/m2oJiFXwaUI.jpg\",\"width\":807,\"height\":605,\"type\":\"y\"},{\"src\":\"https://pp.userap...1ab/AN21uDZWY3I.jpg\",\"width\":1280,\"height\":960,\"type\":\"z\"}],\"text\":\"\",\"date\":1520372745,\"post_id\":181,\"likes\":{\"user_likes\":0,\"count\":5},\"reposts\":{\"count\":0},\"real_offset\":0}");
            int a = 0;
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        GetMyPhotosCommand command = new GetMyPhotosCommand(
//                1,
//                0,
//                false,
//                false,
//                false,
//                true,
//                true
//        );
//
//        command.execute(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                mPhotosCallbackHandler.obtainMessage(0,"FAIL").sendToTarget();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                mPhotosCallbackHandler.obtainMessage(0, "S U C C E S S").sendToTarget();
//                Log.d("VK_API_TEST", response.body().string());
//            }
//        });
    }
}
