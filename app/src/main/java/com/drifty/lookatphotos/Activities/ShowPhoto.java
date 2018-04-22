package com.drifty.lookatphotos.Activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.drifty.lookatphotos.Fragments.TableOfPhotos;
import com.drifty.lookatphotos.LoadPhotos.LoaderFullPhoto;
import com.drifty.lookatphotos.LoadPhotos.RequestQueueValley;
import com.drifty.lookatphotos.R;

import java.nio.ByteBuffer;

public class ShowPhoto extends AppCompatActivity implements LoaderFullPhoto.CallBack {
    private ImageView photoView;
    private ProgressBar progressBar;
    private Bitmap photo;
    private LoaderFullPhoto lfp;

    private static final String PHOTO_CON = "photo";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String CONFIG = "config";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);
        photoView = findViewById(R.id.photo);
        progressBar = findViewById(R.id.progressBar);
        boolean needLoad = true;
        if (savedInstanceState != null) {
            byte[] photoInBytes = savedInstanceState.getByteArray(PHOTO_CON);
            if (photoInBytes != null) {
                try {
                    Bitmap bitmap = Bitmap.createBitmap(savedInstanceState.getInt(WIDTH)
                            , savedInstanceState.getInt(HEIGHT)
                            , Bitmap.Config.valueOf(savedInstanceState.getString(CONFIG)));
                    bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(photoInBytes));
                    onSuccessLoadPhoto(bitmap);
                    needLoad = false;
                } catch (OutOfMemoryError error) {

                }
            }
        }
        if (needLoad) {
            lfp = new LoaderFullPhoto(RequestQueueValley.getInstance(), this);
            lfp.getPhoto(getIntent().getStringExtra(TableOfPhotos.URL));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (photo != null) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(photo.getByteCount());
            photo.copyPixelsToBuffer(byteBuffer);
            outState.putByteArray(PHOTO_CON, byteBuffer.array());
            outState.putString(CONFIG, photo.getConfig().name());
            outState.putInt(WIDTH, photo.getWidth());
            outState.putInt(HEIGHT, photo.getHeight());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    public void onFailedLoadPhoto(String error) {
        progressBar.setVisibility(View.INVISIBLE);
        final ConstraintLayout cl = findViewById(R.id.notification_of_error);
        cl.setVisibility(View.VISIBLE);
        final Button button = cl.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cl.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                lfp.getPhoto(getIntent().getStringExtra(TableOfPhotos.URL));
            }
        });
    }
}
