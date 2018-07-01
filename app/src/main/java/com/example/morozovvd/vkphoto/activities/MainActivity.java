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
import com.example.morozovvd.vkphoto.commands.GetMyPhotosCommand;
import com.example.morozovvd.vkphoto.objects.PhotoMeta;
import com.example.morozovvd.vkphoto.objects.PhotoMetasResponse;
import com.example.morozovvd.vkphoto.tasks.VkApiTask;

import java.lang.ref.WeakReference;
import java.util.List;

public class MainActivity extends AppCompatActivity implements VkApiTask.ResponseHandler {

    public static final int AUTH_REQUEST = 1;
    public static final String FETCH_NEXT_PAGE = "FETCH_NEXT_PAGE";

    public static final int COLUMN_COUNT = 4;
    public static final int PAGE_SIZE = 24;
    public static final PhotoMeta.Copy.Type COPY_TYPE_FOR_PREVIEW = PhotoMeta.Copy.Type.CUT_320;

    private RecyclerView mPhotosRecyclerView;
    private GridLayoutManager mLayoutManager;
    private PhotoRecyclerAdapter mPhotoRecyclerAdapter;

    private boolean loadingInProgress = false;
    private boolean loadedAll = false;
    private int currentPage = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhotosRecyclerView = findViewById(R.id.photos_recycler_view);

        //todo: вспомнить, зачем это
        mPhotosRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, COLUMN_COUNT);
        mPhotosRecyclerView.setLayoutManager(mLayoutManager);

        mPhotoRecyclerAdapter = new PhotoRecyclerAdapter(PhotoManager.getInstance().getThumbnailCache());
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
                    onScrolledToLast();
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

    private void onScrolledToLast() {
        loadNext();
    }

    private void loadNext() {
        if (loadingInProgress || loadedAll) return;

        loadingInProgress = true;
        currentPage++;
        //для увеличения производительности можно сделать команды мутабельными и
        //переиспользовать команду, меняя count и offset
        GetMyPhotosCommand command = new GetMyPhotosCommand(
                PAGE_SIZE,
                PAGE_SIZE * currentPage,
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
                loadingInProgress = false;
                PhotoMetasResponse photoMetasResponse = (PhotoMetasResponse) response;
                List<PhotoMeta> photoMetas = photoMetasResponse.getPhotoMetas();
                loadedAll = photoMetas.isEmpty();
                if (loadedAll) break;
                mPhotoRecyclerAdapter.addPhotoMetas(photoMetas);
                PhotoManager.getInstance().getPhotoMetas().addAll(photoMetas);
                break;
            default:
                //do nothing
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTH_REQUEST && resultCode == RESULT_OK) {
            NetworkHelper.getInstance().setToken(data.getStringExtra(OauthActivity.EXTRA_TOKEN));
            loadNext();
        } else {
            //todo: show auth error
        }
    }
}
