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
import com.drifty.lookatphotos.Fragments.Adapters.PhotoEntityAdapter;
import com.drifty.lookatphotos.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.drifty.lookatphotos.LoadPhotos.Tools.CalculatorSizeOfPhoto;
import com.drifty.lookatphotos.LoadPhotos.LoaderInfoAboutPhotos;
import com.drifty.lookatphotos.LoadPhotos.Tools.PhotoEntity;
import com.drifty.lookatphotos.LoadPhotos.Tools.RequestQueueValley;

public class TableOfPhotos extends Fragment implements LoaderInfoAboutPhotos.CallBack {
    private int count;
    private boolean isPortrait;
    private String typeOfDelivery;
    private String typeOfPhotos;
    private String fieldForTime;

    private List<PhotoEntity> photos;

    private LoaderInfoAboutPhotos loader;
    private PhotosCache photosCache;

    public final static String URL = "url";

    private RecyclerView recyclerView;
    private PhotoEntityAdapter adapter;

    private boolean isLoading;

    private int expectPhotos;
    private List<PhotoEntity> errorPhotos;
    private PhotoEntity lastPhotoByTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.fragment_table_of_photos, container, false);
        expectPhotos = 0;
        errorPhotos = new ArrayList<>();
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

        isLoading = true;
        photos = new ArrayList<>();
        photos.add(null);
        initRecyclerView(fragView, countPhotoInLine);
        if (savedInstanceState == null) {
            loader.getInfoAboutPhoto(count, fieldForTime);
        } else {
            lastPhotoByTime = photosCache.getLastPhotoByTime();
            List<PhotoEntity> savedPhoto = photosCache.getListPhotoEntity(typeOfPhotos);
            if (savedPhoto != null && !savedPhoto.isEmpty()) {
                expectPhotos = savedPhoto.size();
                photos.addAll(getLastIndexOfPhotos(), savedPhoto);
                for (PhotoEntity pe : savedPhoto) {
                    if (getCurrentIcon(pe) == null) {
                        loader.getPhoto(getCurrentUrl(pe), pe);
                    } else {
                        photos.remove(getLastIndexOfPhotos());
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                        break;
                    }
                }
            } else {
                loader.getInfoAboutPhoto(count, fieldForTime);
            }
        }
        return fragView;
    }

    private void initRecyclerView(View fragView, int countPhotoInLine) {
        recyclerView = fragView.findViewById(R.id.recyclerView);
        adapter = new PhotoEntityAdapter(getContext(), photos, isPortrait);
        final GridLayoutManager glm = new GridLayoutManager(getContext(), countPhotoInLine);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int size = 0;
                switch (adapter.getItemViewType(position)) {
                    case PhotoEntityAdapter.LOADING_VIEW:
                        size = 2;
                        break;
                    case PhotoEntityAdapter.PHOTO_VIEW:
                        size = 1;

                }
                return size;
            }
        });
        recyclerView.setLayoutManager(glm);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = glm.getItemCount();
                int lastVisibleItem = glm.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount - 1 == lastVisibleItem) {
                    isLoading = true;
                    photos.add(null);
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyItemInserted(getLastIndexOfPhotos());
                        }
                    });
                    loader.getInfoAboutPhoto(typeOfDelivery, fieldForTime, lastPhotoByTime.getTime(), lastPhotoByTime.getId(), lastPhotoByTime.getUid(), count + 1);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loader.stopLoading();
        if (photos.size() > 1) {
            if (photos.get(getLastIndexOfPhotos()) == null) {
                photos.remove(getLastIndexOfPhotos());
            }
            photosCache.setLastPhotoByTime(lastPhotoByTime);
            photosCache.setListPhotoEntity(typeOfPhotos, photos);
        }
    }

    @Override
    public synchronized void onSuccessLoadPhoto(Bitmap photo, PhotoEntity pe) {
        if (isPortrait) {
            pe.setPortraitIcon(photo);
        } else {
            pe.setLandscapeIcon(photo);
        }
        expectPhotos--;
        photosLoaded();
    }

    @Override
    public synchronized void onFailedLoadPhoto(PhotoEntity pe) {
        errorPhotos.add(pe);
        photosLoaded();
    }

    private void photosLoaded() {
        if (expectPhotos == errorPhotos.size()) {
            expectPhotos = 0;
            photos.remove(getLastIndexOfPhotos());
            photos.removeAll(errorPhotos);
            errorPhotos.clear();
            lastPhotoByTime = photos.get(getLastIndexOfPhotos());
            for (int i = photos.size() - 2; i >= 0 && i > photos.size() - count; i--) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                try {
                    if (sdf.parse(lastPhotoByTime.getTime()).compareTo(sdf.parse(photos.get(i).getTime())) > 0) {
                        lastPhotoByTime = photos.get(i);
                    }
                } catch (ParseException e) {

                }
            }
            adapter.notifyDataSetChanged();
            isLoading = false;
        }
    }

    @Override
    public void onSuccessLoadInfoAboutPhoto(List<PhotoEntity> photos) {
        if (this.photos.size() > 1) {
            photos.remove(0);
        }
        if (photos.isEmpty()) {
            this.photos.remove(getLastIndexOfPhotos());
            adapter.notifyDataSetChanged();
            isLoading = false;
        } else {
            expectPhotos = photos.size();
            Collections.sort(photos, new Comparator<PhotoEntity>() {
                @Override
                public int compare(PhotoEntity pe1, PhotoEntity pe2) {
                    return pe1.getHeight() - pe2.getHeight();
                }
            });
            this.photos.addAll(getLastIndexOfPhotos(), photos);
            for (PhotoEntity pe : photos) {
                loader.getPhoto(getCurrentUrl(pe), pe);
            }
        }
    }

    @Override
    public void onFailedLoadInfoAboutPhoto() {
        if (photos.size() == 1) {

        } else {

        }
    }

    private String getCurrentUrl(PhotoEntity pe) {
        return isPortrait ? pe.getPortraitIconUrl() : pe.getLandscapeIconUrl();
    }

    private Bitmap getCurrentIcon(PhotoEntity pe) {
        return isPortrait ? pe.getPortraitIcon() : pe.getLandscapeIcon();
    }

    private int getLastIndexOfPhotos() {
        return photos.size() - 1;
    }
}