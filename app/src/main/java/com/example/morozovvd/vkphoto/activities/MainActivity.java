package com.example.morozovvd.vkphoto.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.morozovvd.vkphoto.NetworkHelper;
import com.example.morozovvd.vkphoto.PhotoRecyclerAdapter;
import com.example.morozovvd.vkphoto.R;
import com.example.morozovvd.vkphoto.tasks.MyTask;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    static final int AUTH_REQUEST = 1;

    private RecyclerView mPhotosRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhotosRecyclerView = findViewById(R.id.photos_recycler_view);

        mPhotosRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, 2);
        mPhotosRecyclerView.setLayoutManager(mLayoutManager);

        int[] photoResourceIds = new int[8];
        photoResourceIds[0] = R.drawable.sample_0;
        photoResourceIds[1] = R.drawable.sample_1;
        photoResourceIds[2] = R.drawable.sample_2;
        photoResourceIds[3] = R.drawable.sample_3;
        photoResourceIds[4] = R.drawable.sample_4;
        photoResourceIds[5] = R.drawable.sample_5;
        photoResourceIds[6] = R.drawable.sample_6;
        photoResourceIds[7] = R.drawable.sample_7;

        PhotoRecyclerAdapter adapter = new PhotoRecyclerAdapter(photoResourceIds);
        mPhotosRecyclerView.setAdapter(adapter);


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
            MyTask myTask = new MyTask(new WeakReference<>(this));
            myTask.execute();
        }
    }

    public void showToast() {
        Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show();
    }
}
