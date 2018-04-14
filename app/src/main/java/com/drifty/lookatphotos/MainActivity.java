package com.drifty.lookatphotos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    private int topPadding = 2;
    private int countPhotoInLine = 2;
    private int count = 15;
    private boolean isPortrait;

    private List<PhotoEntity> photos;
    private LoaderPhotos loader;

    private ScrollView scroll;
    private TableLayout table;
    private TableRow loadingRow;
    private final TableRow.LayoutParams sizeOfRows = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

    private boolean isLoading = true;
    private int currentLine = 0;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSizeScreen();
        initScroll();
        initLoadingRow();
        table = findViewById(R.id.table);
        table.addView(loadingRow);
        RequestQueue rq = RequestQueueValley.getInstance(this);
        CalculatorSizeOfPhoto csop = new CalculatorSizeOfPhoto(widthScreen, heightScreen, countPhotoInLine);
        loader = new LoaderPhotos(rq, ValueTypeOfPhotos.NEW_INTERESTING_PHOTOS, csop, this);
        loader.getInfoAboutPhoto(count);
    }

    private void initSizeScreen() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        topPadding *= getResources().getDisplayMetrics().density;
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

    private void initScroll() {
        scroll = findViewById(R.id.scroll);
        scroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (scroll.getChildAt(scroll.getChildCount() - 1).getBottom() - (scroll.getHeight() + scroll.getScrollY()) <= 0 && !isLoading) {
                    isLoading = true;
                    if (currentIndex == 0) {
                        table.addView(loadingRow);
                    } else {
                        changeStateInEmptyIcon(View.VISIBLE);
                    }
                    PhotoEntity pe = photos.get(photos.size() - 1);
                    loader.getInfoAboutPhoto(ValueTypeOfDelivery.UPDATED, ValueTypeOfDelivery.UPDATED, pe.getTime(), pe.getId(), pe.getUid(), count + 1);
                }
            }
        });
    }

    private void initLoadingRow() {
        loadingRow = newTableRow();
        ProgressBar pb = new ProgressBar(this);
        pb.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        loadingRow.addView(pb);
    }

    private TableRow newTableRow() {
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(sizeOfRows);
        tr.setPadding(0, topPadding, 0, 0);
        return tr;
    }

    private void createRows(int countPhotos) {
        int needRow;
        if (currentIndex != 0) {
            countPhotos -= countPhotoInLine - currentIndex;
        }
        needRow = (int) Math.ceil((double) countPhotos / countPhotoInLine);
        for (int i = 0; i < needRow; i++) {
            TableRow tr = newTableRow();
            for (int j = 0; j < countPhotoInLine; j++) {
                tr.addView(getLayoutInflater().inflate(R.layout.icon_of_photo, tr, false), j);
            }
            table.addView(tr);
        }
    }


    private void initIconOfPhoto(View view, Bitmap photo, final PhotoEntity pe) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pe.getOrigUrl() != null) {
                    Intent i = new Intent(MainActivity.this, ShowPhoto.class);
                    startActivity(i);
                }
            }
        });
        ((ImageView) view.findViewById(R.id.photo)).setImageBitmap(photo);
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    private void changeStateInEmptyIcon(int code) {
        int indexPbForOff = currentIndex;
        while (indexPbForOff != countPhotoInLine) {
            ((TableRow) table.getChildAt(currentLine))
                    .getChildAt(indexPbForOff)
                    .findViewById(R.id.progressBar)
                    .setVisibility(code);
            indexPbForOff++;
        }
    }

    private void showPhotos(List<PhotoEntity> photos) {
        createRows(photos.size());
        for (PhotoEntity pe : photos) {
            loader.getPhoto(isPortrait ? pe.getPortraitIconUrl() : pe.getLandscapeIconUrl(), pe);
        }
    }

    @Override
    public synchronized void onSuccessLoadPhoto(Bitmap photo, final PhotoEntity pe) {
        if (isPortrait) {
            pe.setPortraitIcon(photo);
        } else {
            pe.setLandscapeIcon(photo);
        }
        initIconOfPhoto(((TableRow) table.getChildAt(currentLine)).getChildAt(currentIndex), photo, pe);
        currentIndex++;
        if (currentLine * countPhotoInLine + currentIndex == photos.size()) {
            isLoading = false;
            changeStateInEmptyIcon(View.INVISIBLE);
        }
        if (currentIndex == countPhotoInLine) {
            currentLine++;
            currentIndex = 0;
        }
    }

    @Override
    public void onFailedLoadPhoto(String error) {
        Log.d("debug", error);
    }

    @Override
    public void onSuccessLoadInfoAboutPhoto(List<PhotoEntity> photos) {
        table.removeView(loadingRow);
        if (this.photos == null) {
            this.photos = photos;
            showPhotos(photos);
        } else if (photos.size() > 1) {
            photos.remove(0);
            this.photos.addAll(photos);
            showPhotos(photos);
        } else {
            isLoading = false;
        }
    }

    @Override
    public void onFailedLoadInfoAboutPhoto(String error) {
        Log.d("debug", error);
    }
}
