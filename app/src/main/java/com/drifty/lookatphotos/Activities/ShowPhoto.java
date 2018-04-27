package com.drifty.lookatphotos.Activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.drifty.lookatphotos.ApplicationContext.PhotosCache;
import com.drifty.lookatphotos.Fragments.TableOfPhotos;
import com.drifty.lookatphotos.LoadPhotos.LoaderFullPhoto;
import com.drifty.lookatphotos.LoadPhotos.Tools.RequestQueueValley;
import com.drifty.lookatphotos.R;

public class ShowPhoto extends AppCompatActivity implements LoaderFullPhoto.CallBack {
    private ImageView photoView;
    private ProgressBar progressBar;
    private Bitmap photo;
    private LoaderFullPhoto lfp;
    private PhotosCache photosCache;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);
        photoView = findViewById(R.id.photo);
        progressBar = findViewById(R.id.progressBar);
        photosCache = (PhotosCache) getApplicationContext();
        photo = photosCache.getCurrentPhoto();
        url = getIntent().getStringExtra(TableOfPhotos.URL);
        if (photo == null) {
            lfp = new LoaderFullPhoto(RequestQueueValley.getInstance(), this);
            lfp.getPhoto(url);
        } else {
            onSuccessLoadPhoto(photo);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        photosCache.setCurrentPhoto(photo);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        photosCache.setCurrentPhoto(null);
        if (photo == null && lfp != null) {
            lfp.cancelLoadPhoto();
        }
    }

    @Override
    public void onSuccessLoadPhoto(Bitmap photo) {
        this.photo = photo;
        this.photoView.setImageBitmap(photo);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFailedLoadPhoto() {
        progressBar.setVisibility(View.INVISIBLE);
        final ConstraintLayout cl = findViewById(R.id.notification_of_error);
        cl.setVisibility(View.VISIBLE);
        final Button button = cl.findViewById(R.id.repeatBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cl.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                lfp.getPhoto(url);
            }
        });
    }
}
