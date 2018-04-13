package com.drifty.lookatphotos;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.android.volley.RequestQueue;

import java.util.List;

import LoadPhotos.CalculatorSizeOfPhoto;
import LoadPhotos.LoaderPhotos;
import LoadPhotos.MetaData.ValueTypeOfDelivery;
import LoadPhotos.MetaData.ValueTypeOfPhotos;
import LoadPhotos.PhotoEntity;
import LoadPhotos.RequestQueueValley;

public class MainActivity extends AppCompatActivity implements LoaderPhotos.CallBack {
    private int widthScreen;
    private int heightScreen;
    private int countPhotoInLine = 2;
    private int count = 30;
    private boolean isPortrait;

    private List<PhotoEntity> photos;
    private LoaderPhotos loader;

    private ScrollView scroll;
    private TableLayout table;
    private final TableRow.LayoutParams sizeOfRows = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

    private boolean isLoading;
    private int currentLine = 0;
    private int currentIndex = 0;

    String name = "debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSizeScreen();
        initScroll();
        table = findViewById(R.id.table);
        createRows();
        RequestQueue rq = RequestQueueValley.getInstance(this);
        CalculatorSizeOfPhoto csop = new CalculatorSizeOfPhoto(widthScreen, heightScreen, countPhotoInLine);
        loader = new LoaderPhotos(rq, ValueTypeOfPhotos.NEW_INTERESTING_PHOTOS, csop, this);
        loader.getInfoAboutPhoto(count);
    }

    private void initScroll() {
        scroll = findViewById(R.id.scroll);
        scroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (scroll.getChildAt(scroll.getChildCount() - 1).getBottom() - (scroll.getHeight() + scroll.getScrollY()) <= 0 && !isLoading) {
                    isLoading = true;
                    createRows();
                    PhotoEntity pe = photos.get(photos.size() - 1);
                    loader.getInfoAboutPhoto(ValueTypeOfDelivery.UPDATED, ValueTypeOfDelivery.UPDATED, pe.getTime(), pe.getId(), pe.getUid(), count);
                    Log.d(name, "start");
                }
            }
        });
    }

    private void createRows() {
        for (int i = 0; i < count; i++) {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(sizeOfRows);
            table.addView(tr);
            for (int j = 0; j < countPhotoInLine; j++) {
                tr.addView(getLayoutInflater().inflate(R.layout.icon_of_photo, tr, false), j);
            }
        }
    }

    private void initSizeScreen() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                isPortrait = true;
                widthScreen = size.x;
                heightScreen = size.y;
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                isPortrait = false;
                widthScreen = size.y;
                heightScreen = size.x;
        }
    }

    @Override
    public synchronized void onSuccessLoadPhoto(Bitmap photo, PhotoEntity pe) {
        if (isPortrait) {
            pe.setPortraitIcon(photo);
        } else {
            pe.setLandscapeIcon(photo);
        }
        if (currentIndex == countPhotoInLine) {
            currentLine++;
            currentIndex = 0;
        }
        TableRow tr = (TableRow) table.getChildAt(currentLine);
        View view = tr.getChildAt(currentIndex);
        ((ImageView) view.findViewById(R.id.photo)).setImageBitmap(photo);
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        currentIndex++;
        if ((currentLine + 1) * (currentIndex + 1) == photos.size()) {
            isLoading = false;
        }
    }

    @Override
    public void onFailedLoadPhoto(String error) {
        System.out.println(error);
    }

    @Override
    public void onSuccessLoadInfoAboutPhoto(List<PhotoEntity> photos) {
        if (this.photos == null) {
            this.photos = photos;
        } else {
            this.photos.addAll(photos);
        }
        for (PhotoEntity pe : photos) {
            loader.getPhoto(isPortrait ? pe.getPortraitIconUrl() : pe.getLandscapeIconUrl(), pe);
        }
    }

    @Override
    public void onFailedLoadInfoAboutPhoto(String error) {
        System.out.println(error);
    }
}
