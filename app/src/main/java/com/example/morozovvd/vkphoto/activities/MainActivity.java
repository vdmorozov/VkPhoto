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
import com.example.morozovvd.vkphoto.commands.GetMyPhotosCommand;
import com.example.morozovvd.vkphoto.objects.Photo;
import com.example.morozovvd.vkphoto.objects.PhotoResponse;
import com.example.morozovvd.vkphoto.tasks.VkApiTask;

import java.lang.ref.WeakReference;
import java.util.List;

public class MainActivity extends AppCompatActivity implements VkApiTask.ResponseHandler {

    public static final int COLUMN_COUNT = 4;
    static final int AUTH_REQUEST = 1;
    public static final String FETCH_NEXT_PAGE = "FETCH_NEXT_PAGE";
    public static final int PAGE_SIZE = 24;
    public static final Photo.Copy.Type COPY_TYPE_FOR_PREVIEW = Photo.Copy.Type.CUT_320;

    private RecyclerView mPhotosRecyclerView;
    private GridLayoutManager mLayoutManager;
    private PhotoRecyclerAdapter mPhotoRecyclerAdapter;

    private int mPage = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhotosRecyclerView = findViewById(R.id.photos_recycler_view);

        mPhotosRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, COLUMN_COUNT);
        mPhotosRecyclerView.setLayoutManager(mLayoutManager);

//        int[] photoResourceIds = new int[8];
//        photoResourceIds[0] = R.drawable.sample_0;
//        photoResourceIds[1] = R.drawable.sample_1;
//        photoResourceIds[2] = R.drawable.sample_2;
//        photoResourceIds[3] = R.drawable.sample_3;
//        photoResourceIds[4] = R.drawable.sample_4;
//        photoResourceIds[5] = R.drawable.sample_5;
//        photoResourceIds[6] = R.drawable.sample_6;
//        photoResourceIds[7] = R.drawable.sample_7;

        mPhotoRecyclerAdapter = new PhotoRecyclerAdapter();
        mPhotoRecyclerAdapter.addOnPhotoClickListener(new PhotoRecyclerAdapter.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(Photo photo) {
                Intent intent = FullscreenActivity.getCallingIntent(MainActivity.this, photo);
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
                    fetchNextPage();
                    //mPresenter.onScrolledToLast();
                }
            }
        });


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
            fetchNextPage();
        } else {
            //todo: show auth error
        }
    }

    public void fetchNextPage() {
        mPage++;
        //для увеличения производительности можно сделать команды мутабельными и
        //переиспользовать команду, меняя count и offset
        GetMyPhotosCommand command = new GetMyPhotosCommand(
                PAGE_SIZE,
                PAGE_SIZE * mPage,
                false,
                false,
                true,
                false,
                false
        );

        VkApiTask getPhotoListTask = new VkApiTask(
                command,
                FETCH_NEXT_PAGE,
                new WeakReference<>((VkApiTask.ResponseHandler) this)
        );
        getPhotoListTask.execute();
    }

    @Override
    public void onVkApiTaskResponse(Object response, String commandId) {
        switch (commandId) {
            case FETCH_NEXT_PAGE:
                PhotoResponse photoResponse = (PhotoResponse) response;
                List<Photo> photoList = photoResponse.getList();
                mPhotoRecyclerAdapter.addPhotos(photoList);
                break;
            default:
                //do nothing
        }
    }
}
