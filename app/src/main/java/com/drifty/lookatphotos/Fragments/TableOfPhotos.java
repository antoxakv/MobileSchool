package com.drifty.lookatphotos.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drifty.lookatphotos.ApplicationContext.PhotosCache;
import com.drifty.lookatphotos.R;

import java.util.ArrayList;
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
    private String typeOfDelivery;
    private String typeOfPhotos;
    private String fieldForTime;

    private List<PhotoEntity> photos = new ArrayList<>();

    private LoaderInfoAboutPhotos loader;
    private PhotosCache photosCache;

    public final static String URL = "url";

    private RecyclerView recyclerView;
    private MyAdapter adapter;

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

        CalculatorSizeOfPhoto csop = new CalculatorSizeOfPhoto(widthScreen, heightScreen, countPhotoInLine);
        loader = new LoaderInfoAboutPhotos(RequestQueueValley.getInstance(), typeOfPhotos, csop, this);
        photosCache = (PhotosCache) getActivity().getApplicationContext();

        photos = photosCache.getListPhotoEntity(typeOfPhotos);
        if(photos == null){
            photos = new ArrayList<>();
        }
        initRecyclerView(fragView);
        if (savedInstanceState == null || photos.isEmpty()) {
            loader.getInfoAboutPhoto(count, fieldForTime);
        } else {

        }
        return fragView;
    }

    private void initRecyclerView(View fragView) {
        recyclerView = fragView.findViewById(R.id.recyclerView);
        GridLayoutManager glm = new GridLayoutManager(getContext(), countPhotoInLine);
        GridLayoutManager.LayoutParams lp = new GridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        glm.generateLayoutParams(lp);
        recyclerView.setLayoutManager(glm);
        adapter = new MyAdapter(getContext(), photos, isPortrait);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        photosCache.setListPhotoEntity(typeOfPhotos, photos);
    }

    @Override
    public synchronized void onSuccessLoadPhoto(Bitmap photo, PhotoEntity pe) {
        if (isPortrait) {
            pe.setPortraitIcon(photo);
        } else {
            pe.setLandscapeIcon(photo);
        }
        adapter.setPhotoEntities(photos);
    }

    @Override
    public synchronized void onFailedLoadPhoto(PhotoEntity pe) {

    }

    @Override
    public void onSuccessLoadInfoAboutPhoto(List<PhotoEntity> photos) {
        this.photos.addAll(photos);
        for (PhotoEntity pe : photos) {
            loader.getPhoto(isPortrait ? pe.getPortraitIconUrl() : pe.getLandscapeIconUrl(), pe);
        }
    }

    @Override
    public void onFailedLoadInfoAboutPhoto() {

    }
}