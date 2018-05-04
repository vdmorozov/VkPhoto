package com.example.morozovvd.vkphoto.activities;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.example.morozovvd.vkphoto.tasks.ImageDownloadTask;
import com.example.morozovvd.vkphoto.tasks.VkApiTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;

public class MainActivity extends AppCompatActivity implements VkApiTask.ResponseHandler, ImageDownloadTask.ResponseHandler {

    static final int AUTH_REQUEST = 1;
    public static final String VK_PHOTO_LIST_TASK = "VK_PHOTO_LIST_TASK";

    private RecyclerView mPhotosRecyclerView;
    private GridLayoutManager mLayoutManager;
    private PhotoRecyclerAdapter mPhotoRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhotosRecyclerView = findViewById(R.id.photos_recycler_view);

        mPhotosRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, 2);
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
        mPhotosRecyclerView.setAdapter(mPhotoRecyclerAdapter);

        mPhotosRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int position = mLayoutManager.findLastVisibleItemPosition();

                if (position == mPhotoRecyclerAdapter.getItemCount() - 1) {
                    //todo: download and add photos
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

            GetMyPhotosCommand command = new GetMyPhotosCommand(
                    1,
                    0,
                    false,
                    false,
                    true,
                    true,
                    true
            );

            VkApiTask getPhotoListTask = new VkApiTask(
                    command,
                    VK_PHOTO_LIST_TASK,
                    new WeakReference<>((VkApiTask.ResponseHandler) this)
            );
            getPhotoListTask.execute();
        }
    }

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVkApiTaskResponse(Object response, String commandId) {
        switch (commandId) {
            case VK_PHOTO_LIST_TASK:
                //todo: do something
                showToast("got list");

                PhotoResponse photoResponse = (PhotoResponse) response;
                List<Photo> photoList = photoResponse.getList();

                if (photoList.size() < 1) break;

                Photo photo = photoList.get(0);
                String urlString = photo.getCopy(Photo.Copy.Type.CUT_320).getUrl();
                HttpUrl url = HttpUrl.parse(urlString);
                ImageDownloadTask imageDownloadTask = new ImageDownloadTask(
                        url,
                        0,
                        new WeakReference<ImageDownloadTask.ResponseHandler>(this)
                );
                imageDownloadTask.execute();
                break;
            default:
                //do nothing
        }
    }

    @Override
    public void onImageDownloaded(Bitmap image, int imageId, HttpUrl imageUrl) {
        List<Bitmap> bitmaps = new ArrayList<>();
        bitmaps.add(image);
        mPhotoRecyclerAdapter.setPhotos(bitmaps);
    }
}
