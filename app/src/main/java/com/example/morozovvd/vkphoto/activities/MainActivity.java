package com.example.morozovvd.vkphoto.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.morozovvd.vkphoto.NetworkHelper;
import com.example.morozovvd.vkphoto.PhotoManager;
import com.example.morozovvd.vkphoto.adapters.PhotoRecyclerAdapter;
import com.example.morozovvd.vkphoto.R;
import com.example.morozovvd.vkphoto.objects.PhotoMeta;

public class MainActivity extends AppCompatActivity {

    public static final int COLUMN_COUNT = 4;
    static final int AUTH_REQUEST = 1;
    public static final PhotoMeta.Copy.Type COPY_TYPE_FOR_PREVIEW = PhotoMeta.Copy.Type.CUT_320;

    private RecyclerView mPhotosRecyclerView;
    private GridLayoutManager mLayoutManager;
    private PhotoRecyclerAdapter mPhotoRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhotosRecyclerView = findViewById(R.id.photos_recycler_view);

        mPhotosRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, COLUMN_COUNT);
        mPhotosRecyclerView.setLayoutManager(mLayoutManager);

        mPhotoRecyclerAdapter = new PhotoRecyclerAdapter(PhotoManager.getInstance());
        mPhotoRecyclerAdapter.setOnPhotoClickListener(new PhotoRecyclerAdapter.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(PhotoMeta photoMeta, int position) {
                Intent intent = FullscreenActivity.getCallingIntent(MainActivity.this, position);
                startActivity(intent);
            }
        });

        mPhotosRecyclerView.setAdapter(mPhotoRecyclerAdapter);

        mPhotosRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int position = mLayoutManager.findLastVisibleItemPosition();

                if (position == mPhotoRecyclerAdapter.getItemCount() - 1) {
                    //todo: download and add photos
                    PhotoManager.getInstance().fetchNextPage();
                }
            }
        });


        //todo: отслеживание состояния авторизации
        boolean authorized = (NetworkHelper.getInstance().getToken() != null);

        if (!authorized) {
            Intent oauthActivityIntent = new Intent(this, OauthActivity.class);
            startActivityForResult(oauthActivityIntent, AUTH_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTH_REQUEST && resultCode == RESULT_OK) {
            NetworkHelper.getInstance().setToken(data.getStringExtra(OauthActivity.EXTRA_TOKEN));
            PhotoManager.getInstance().fetchNextPage();
        } else {
            //todo: show auth error
        }
    }
}
