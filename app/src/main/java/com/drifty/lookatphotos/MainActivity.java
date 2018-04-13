package com.drifty.lookatphotos;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.android.volley.RequestQueue;

import java.util.ArrayList;
import java.util.List;

import LoadPhotos.CalculatorSizeOfPhoto;
import LoadPhotos.LoaderPhotos;
import LoadPhotos.MetaData.ValueTypeOfPhotos;
import LoadPhotos.PhotoEntity;
import LoadPhotos.RequestQueueValley;

public class MainActivity extends AppCompatActivity implements LoaderPhotos.CallBack {
    private int widthScreen;
    private int heightScreen;
    private int countPhotoInLine = 2;
    private int count = 30;
    private List<PhotoEntity> photos;
    private LoaderPhotos lp;
    private TableLayout tl;
    private final TableRow.LayoutParams sizeOfTr = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

    private List<TableRow> tableRows = new ArrayList<>(count);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSizeScreen();
        tl = findViewById(R.id.table);
        RequestQueue rq = RequestQueueValley.getInstance(this);
        CalculatorSizeOfPhoto csop = new CalculatorSizeOfPhoto(widthScreen, heightScreen, countPhotoInLine);
        lp = new LoaderPhotos(rq, ValueTypeOfPhotos.NEW_INTERESTING_PHOTOS, csop, this);
        lp.getInfoAboutPhoto(count);
    }

    private TableRow newTableRow() {
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(sizeOfTr);
        return tr;
    }

    private void initSizeScreen() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthScreen = size.x;
        heightScreen = size.y;
    }

    @Override
    public void onSuccessLoadPhoto(Bitmap photo, PhotoEntity pe) {
        pe.setPortraitIcon(photo);
        TableRow tr;
        int id = 0;
        if (tableRows.isEmpty()) {
            tr = newTableRow();
            tableRows.add(tr);
            tl.addView(tr);
        } else {
            tr = tableRows.get(tableRows.size() - 1);
            if (tr.getChildCount() == countPhotoInLine) {
                tr = newTableRow();
                tableRows.add(tr);
                tl.addView(tr);
            }else{
                id = 1;
            }
        }
        View view = getLayoutInflater().inflate(R.layout.icon_of_photo, null);
        ((ImageView) view.findViewById(R.id.photo)).setImageBitmap(photo);
        tr.addView(view, id);
    }

    @Override
    public void onFailedLoadPhoto(String error) {

    }

    @Override
    public void onSuccessLoadInfoAboutPhoto(List<PhotoEntity> photos) {
        this.photos = photos;
        for (PhotoEntity pe : photos) {
            lp.getPhoto(pe.getPortraitIconUrl(), pe);
        }
    }

    @Override
    public void onFailedLoadInfoAboutPhoto(String error) {
    }
}
