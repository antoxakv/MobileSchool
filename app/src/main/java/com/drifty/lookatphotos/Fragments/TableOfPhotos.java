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
    private PhotoEntityAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.fragment_table_of_photos, container, false);

        Bundle bundle = getArguments();
        count = bundle.getInt(BundleFields.COUNT);
        isPortrait = bundle.getBoolean(BundleFields.IS_PORTRAIT);
        typeOfDelivery = bundle.getString(BundleFields.TYPE_OF_DELIVERY);
        typeOfPhotos = bundle.getString(BundleFields.TYPE_OF_PHOTOS);
        fieldForTime = bundle.getString(BundleFields.FIELD_FOR_TIME);
        int countPhotoInLine = bundle.getInt(BundleFields.COUNT_PHOTO_IN_LINE);
        CalculatorSizeOfPhoto csop = new CalculatorSizeOfPhoto(bundle.getInt(BundleFields.WIDTH_SCREEN),
                bundle.getInt(BundleFields.HEIGHT_SCREEN),
                countPhotoInLine);
        loader = new LoaderInfoAboutPhotos(RequestQueueValley.getInstance(), typeOfPhotos, csop, this);
        photosCache = (PhotosCache) getActivity().getApplicationContext();

        photos = photosCache.getListPhotoEntity(typeOfPhotos);
        if (photos == null) {
            photos = new ArrayList<>();
        }
        initRecyclerView(fragView, countPhotoInLine);
        if (savedInstanceState == null || photos.isEmpty()) {
            loader.getInfoAboutPhoto(count, fieldForTime);
        } else {
            for (PhotoEntity pe : photos) {
                Bitmap photo = getCurrentOrig(pe);
                if (photo == null) {
                    loader.getPhoto(getCurrentUrl(pe), pe);
                }
            }
            adapter.notifyDataSetChanged();
        }
        return fragView;
    }

    private void initRecyclerView(View fragView, int countPhotoInLine) {
        recyclerView = fragView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), countPhotoInLine));
        adapter = new PhotoEntityAdapter(getContext(), photos, isPortrait);
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
        adapter.notifyDataSetChanged();
    }

    @Override
    public synchronized void onFailedLoadPhoto(PhotoEntity pe) {

    }

    @Override
    public void onSuccessLoadInfoAboutPhoto(List<PhotoEntity> photos) {
        this.photos.addAll(photos);
        for (PhotoEntity pe : photos) {
            loader.getPhoto(getCurrentUrl(pe), pe);
        }
    }

    @Override
    public void onFailedLoadInfoAboutPhoto() {

    }

    private String getCurrentUrl(PhotoEntity pe) {
        return isPortrait ? pe.getPortraitIconUrl() : pe.getLandscapeIconUrl();
    }

    private Bitmap getCurrentOrig(PhotoEntity pe) {
        return isPortrait ? pe.getPortraitIcon() : pe.getLandscapeIcon();
    }
}