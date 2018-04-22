package com.drifty.lookatphotos.Fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.drifty.lookatphotos.ApplicationContext.PhotosCache;
import com.drifty.lookatphotos.Activities.ShowPhoto;
import com.drifty.lookatphotos.R;

import java.util.List;

import com.drifty.lookatphotos.LoadPhotos.CalculatorSizeOfPhoto;
import com.drifty.lookatphotos.LoadPhotos.LoaderInfoAboutPhotos;
import com.drifty.lookatphotos.LoadPhotos.PhotoEntity;
import com.drifty.lookatphotos.LoadPhotos.RequestQueueValley;

public class TableOfPhotos extends Fragment implements LoaderInfoAboutPhotos.CallBack {
    private int topPadding;
    private int widthScreen;
    private int heightScreen;
    private int countPhotoInLine;
    private int count;
    private boolean isPortrait;

    private List<PhotoEntity> photos;
    private LoaderInfoAboutPhotos loader;
    private ScrollView scroll;
    private TableLayout table;

    private boolean isLoading;
    private int currentLine;
    private int currentIndex;

    private String typeOfDelivery;
    private String typeOfPhotos;
    private String fieldForTime;

    private TableRow loadingRow;
    private final TableRow.LayoutParams sizeOfRows = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

    private PhotosCache photosCache;

    public final static String URL = "url";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.fragment_table_of_photos, container, false);
        Bundle bundle = getArguments();
        widthScreen = bundle.getInt(BundleFields.WIDTH_SCREEN);
        heightScreen = bundle.getInt(BundleFields.HEIGHT_SCREEN);
        countPhotoInLine = bundle.getInt(BundleFields.COUNT_PHOTO_IN_LINE);
        count = bundle.getInt(BundleFields.COUNT);
        isPortrait = bundle.getBoolean(BundleFields.IS_PORTRAIT);
        topPadding = bundle.getInt(BundleFields.TOP_PADDING);
        typeOfDelivery = bundle.getString(BundleFields.TYPE_OF_DELIVERY);
        typeOfPhotos = bundle.getString(BundleFields.TYPE_OF_PHOTOS);
        fieldForTime = bundle.getString(BundleFields.FIELD_FOR_TIME);
        isLoading = true;
        currentLine = 0;
        currentIndex = 0;
        initScroll(fragView);
        initLoadingRow();
        table = fragView.findViewById(R.id.table);
        CalculatorSizeOfPhoto csop = new CalculatorSizeOfPhoto(widthScreen, heightScreen, countPhotoInLine);
        loader = new LoaderInfoAboutPhotos(RequestQueueValley.getInstance(), typeOfPhotos, csop, this);
        photosCache = (PhotosCache) getActivity().getApplicationContext();
        photos = photosCache.getListPhotoEntity(typeOfPhotos);
        if (savedInstanceState == null || photos == null || photos.isEmpty()) {
            table.addView(loadingRow);
            loader.getInfoAboutPhoto(count, fieldForTime);
        } else {
            showPhotos(photos);
        }
        return fragView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        photosCache.setListPhotoEntity(typeOfPhotos, photos);
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
                    loader.getInfoAboutPhoto(typeOfDelivery, fieldForTime, pe.getTime(), pe.getId(), pe.getUid(), count + 1);
                }
            }
        });
    }

    private void initIconOfPhoto(View view, Bitmap photo, final String url) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShowPhoto.class);
                intent.putExtra(URL, url);
                startActivity(intent);
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
            Bitmap photo = isPortrait ? pe.getPortraitIcon() : pe.getLandscapeIcon();
            if (photo == null) {
                loader.getPhoto(isPortrait ? pe.getPortraitIconUrl() : pe.getLandscapeIconUrl(), pe);
            } else {
                onSuccessLoadPhoto(photo, pe);
            }
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
        initIconOfPhoto(((TableRow) table.getChildAt(currentLine)).getChildAt(currentIndex), photo, pe.getOrigUrl());
        currentIndex++;
        if (currentLine * countPhotoInLine + currentIndex == photos.size()) {
            if(scroll.getChildAt(scroll.getChildCount() - 1).getBottom() < heightScreen){
                PhotoEntity lastPe = photos.get(photos.size() - 1);
                loader.getInfoAboutPhoto(typeOfDelivery, fieldForTime, lastPe.getTime(), lastPe.getId(), lastPe.getUid(), count + 1);
            }else{
                isLoading = false;
                changeStateInEmptyIcon(View.INVISIBLE);
            }
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
            changeStateInEmptyIcon(View.INVISIBLE);
        }
    }

    @Override
    public void onFailedLoadInfoAboutPhoto(String error) {
        Log.d("debug", error);
    }
}