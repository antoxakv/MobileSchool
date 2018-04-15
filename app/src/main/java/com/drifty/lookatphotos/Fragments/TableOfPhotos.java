package com.drifty.lookatphotos.Fragments;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.drifty.lookatphotos.Activities.ShowPhoto;
import com.drifty.lookatphotos.R;

import java.util.List;

import LoadPhotos.CalculatorSizeOfPhoto;
import LoadPhotos.LoaderPhotos;
import LoadPhotos.MetaData.ValueTypeOfDelivery;
import LoadPhotos.MetaData.ValueTypeOfPhotos;
import LoadPhotos.PhotoEntity;
import LoadPhotos.RequestQueueValley;

public class TableOfPhotos extends Fragment implements LoaderPhotos.CallBack {
    private int topPadding;
    private int widthScreen;
    private int heightScreen;
    private int countPhotoInLine;
    private int count;
    private boolean isPortrait;

    private List<PhotoEntity> photos;
    private LoaderPhotos loader;
    private ScrollView scroll;
    private TableLayout table;

    private boolean isLoading = true;
    private int currentLine = 0;
    private int currentIndex = 0;

    private TableRow loadingRow;
    private final TableRow.LayoutParams sizeOfRows = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.fragment_table_of_photos, container, false);

        Bundle bundle = getArguments();
        widthScreen = bundle.getInt("widthScreen");
        heightScreen = bundle.getInt("heightScreen");
        countPhotoInLine = bundle.getInt("countPhotoInLine");
        count = bundle.getInt("count");
        isPortrait = bundle.getBoolean("isPortrait");
        topPadding = bundle.getInt("topPadding");

        initScroll(fragView);
        initLoadingRow();
        table = fragView.findViewById(R.id.table);
        table.addView(loadingRow);
        CalculatorSizeOfPhoto csop = new CalculatorSizeOfPhoto(widthScreen, heightScreen, countPhotoInLine);
        loader = new LoaderPhotos(RequestQueueValley.getInstance(), ValueTypeOfPhotos.NEW_INTERESTING_PHOTOS, csop, this);
        loader.getInfoAboutPhoto(count);
        return fragView;
    }

    private TableRow newTableRow() {
        TableRow tr = new TableRow(getContext());
        tr.setLayoutParams(sizeOfRows);
        tr.setPadding(0, topPadding, 0, 0);
        return tr;
    }

    private void initLoadingRow() {
        loadingRow = newTableRow();
        ProgressBar pb = new ProgressBar(getContext());
        pb.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        loadingRow.addView(pb);
    }

    private void initScroll(View fragView) {
        scroll = fragView.findViewById(R.id.scroll);
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

    private void initIconOfPhoto(View view, Bitmap photo, final PhotoEntity pe) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pe.getOrigUrl() != null) {
                    Intent intent = new Intent(getActivity(), ShowPhoto.class);
                    startActivity(intent);
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

    private void createRows(int countPhotos) {
        int needRow;
        if (currentIndex != 0) {
            countPhotos -= countPhotoInLine - currentIndex;
        }
        needRow = (int) Math.ceil((double) countPhotos / countPhotoInLine);
        for (int i = 0; i < needRow; i++) {
            TableRow tr = newTableRow();
            for (int j = 0; j < countPhotoInLine; j++) {
                tr.addView(getActivity().getLayoutInflater().inflate(R.layout.icon_of_photo, tr, false), j);
            }
            table.addView(tr);
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
