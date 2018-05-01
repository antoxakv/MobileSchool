package com.drifty.lookatphotos.Activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;

import com.drifty.lookatphotos.ApplicationContext.PhotosCache;
import com.drifty.lookatphotos.LoadPhotos.LoaderFullPhoto;
import com.drifty.lookatphotos.LoadPhotos.Tools.RequestQueueValley;
import com.drifty.lookatphotos.R;

import java.util.ArrayList;

public class ShowPhoto extends AppCompatActivity implements LoaderFullPhoto.CallBack {

    public static final String URLS = "urls";
    public static final String POSITION = "position";

    private ImageSwitcher imageSwitcher;
    private ProgressBar progressBar;
    private Bitmap photo;
    private LoaderFullPhoto lfp;
    private PhotosCache photosCache;
    private int position;
    private float startX;
    private ArrayList<String> photosUrls;
    private ConstraintLayout notificationOfError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);
        initImageSwitcher();
        initNotificationOfError();
        progressBar = findViewById(R.id.progressBar);
        photosCache = (PhotosCache) getApplicationContext();
        photo = photosCache.getCurrentPhoto();
        lfp = new LoaderFullPhoto(RequestQueueValley.getInstance(), this);
        if (savedInstanceState == null) {
            position = getIntent().getIntExtra(POSITION, -1);
            photosUrls = getIntent().getStringArrayListExtra(URLS);
        } else {
            position = savedInstanceState.getInt(POSITION);
            photosUrls = savedInstanceState.getStringArrayList(URLS);
        }
        if (photo != null) {
            imageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), photo));
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            lfp.getPhoto(photosUrls.get(position));
        }
    }

    private void initNotificationOfError() {
        notificationOfError = findViewById(R.id.notification_of_error);
        Button repeatBtn = notificationOfError.findViewById(R.id.repeatBtn);
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationOfError.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                lfp.getPhoto(photosUrls.get(position));
            }
        });
    }

    private void initImageSwitcher() {
        imageSwitcher = findViewById(R.id.imageSwitcher);
        imageSwitcher.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(ShowPhoto.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new
                        ImageSwitcher.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });
        imageSwitcher.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = motionEvent.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        float direction = startX - motionEvent.getX();
                        boolean positionChanged = false;
                        if (direction > 0 && position != photosUrls.size() - 1) {
                            position++;
                            positionChanged = true;
                        } else if (direction < 0 && position != 0) {
                            position--;
                            positionChanged = true;
                        }
                        if (positionChanged) {
                            imageSwitcher.setImageDrawable(null);
                            progressBar.setVisibility(View.VISIBLE);
                            lfp.getPhoto(photosUrls.get(position));
                        }
                }
                return true;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        photosCache.setCurrentPhoto(photo);
        outState.putStringArrayList(URLS, photosUrls);
        outState.putInt(POSITION, position);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        photosCache.setCurrentPhoto(null);
        lfp.cancelLoadPhoto();
    }

    @Override
    public void onSuccessLoadPhoto(Bitmap photo) {
        this.photo = photo;
        imageSwitcher.setImageDrawable(new BitmapDrawable(getResources(), photo));
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFailedLoadPhoto() {
        progressBar.setVisibility(View.INVISIBLE);
        notificationOfError.setVisibility(View.VISIBLE);
    }
}
