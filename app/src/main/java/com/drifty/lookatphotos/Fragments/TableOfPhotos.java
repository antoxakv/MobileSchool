package com.drifty.lookatphotos.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.drifty.lookatphotos.ApplicationContext.PhotosCache;
import com.drifty.lookatphotos.Fragments.Adapters.PhotoEntityAdapter;
import com.drifty.lookatphotos.Fragments.MetaData.BundleFields;
import com.drifty.lookatphotos.LoadPhotos.LoaderInfoAboutPhotos;
import com.drifty.lookatphotos.LoadPhotos.Tools.CalculatorSizeOfPhoto;
import com.drifty.lookatphotos.LoadPhotos.Tools.PhotoEntity;
import com.drifty.lookatphotos.LoadPhotos.Tools.RequestQueueValley;
import com.drifty.lookatphotos.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class TableOfPhotos extends Fragment implements LoaderInfoAboutPhotos.CallBack {
    private int count;
    private boolean isPortrait;
    private String typeOfDelivery;
    private String typeOfPhotos;
    private String fieldForTime;

    private List<PhotoEntity> photos;
    private int expectPhotos;
    private List<PhotoEntity> errorPhotos;
    private PhotoEntity lastPhotoByTime;

    private LoaderInfoAboutPhotos loader;
    private PhotosCache photosCache;

    private RecyclerView recyclerView;
    private PhotoEntityAdapter adapter;

    private boolean isLoading;
    private boolean continueLoading;

    private ConstraintLayout notificationOfError;

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
        loadPhotos(savedInstanceState == null);
        initNotificationOfError(fragView);
        return fragView;
    }

    private void initNotificationOfError(final View fragView) {
        notificationOfError = fragView.findViewById(R.id.notification_of_error);
        Button repeatBtn = notificationOfError.findViewById(R.id.repeatBtn);
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationOfError.setVisibility(View.INVISIBLE);
                isLoading = true;
                photos.add(null);
                adapter.notifyItemInserted(0);
                loader.getInfoAboutPhoto(count, fieldForTime);
            }
        });
    }

    private void loadPhotos(boolean savedInstanceStateIsNull) {
        if (savedInstanceStateIsNull) {
            loader.getInfoAboutPhoto(count, fieldForTime);
        } else {
            lastPhotoByTime = photosCache.getLastPhotosByTime(typeOfPhotos);
            List<PhotoEntity> savedPhoto = photosCache.getListPhotoEntity(typeOfPhotos);
            if (savedPhoto != null && !savedPhoto.isEmpty()) {
                photos.addAll(getLastIndexOfPhotos(), savedPhoto);
                int startIndexForLoad = -1;
                //Поиск фотографий, у которых нету иконок для нужной ориентации экрана.
                for (int i = 0; i < getLastIndexOfPhotos(); i++) {
                    if (getCurrentIcon(photos.get(i)) == null) {
                        startIndexForLoad = i;
                        break;
                    }
                }
                if (startIndexForLoad == -1) {
                    photos.remove(getLastIndexOfPhotos());
                    isLoading = false;
                    adapter.notifyDataSetChanged();
                } else {
                    //Загрузка недостающих иконок.
                    expectPhotos = photos.size() - startIndexForLoad - 1;
                    for (int i = startIndexForLoad; i < getLastIndexOfPhotos(); i++) {
                        PhotoEntity pe = photos.get(i);
                        loader.getPhoto(getCurrentUrl(pe), pe);
                    }
                }
            } else {
                loader.getInfoAboutPhoto(count, fieldForTime);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //Если загрузка была прервана, то снова загружаем фотографии.
        if (continueLoading) {
            continueLoading = false;
            if (getLastIndexOfPhotos() > 0) {
                //При отсутсвие LoadingViewHolder проиходит его добавление.
                if (photos.get(getLastIndexOfPhotos()) != null) {
                    photos.add(null);
                }
                Iterator<PhotoEntity> iterator = photos.iterator();
                boolean wasNull = false;
                while (iterator.hasNext()) {
                    PhotoEntity pe = iterator.next();
                    if (pe != null && getCurrentIcon(pe) == null) {
                        loader.getPhoto(getCurrentUrl(pe), pe);
                        wasNull = true;
                    }
                }
                if (!wasNull) {
                    photos.remove(getLastIndexOfPhotos());
                    adapter.notifyDataSetChanged();
                    isLoading = false;
                }
            } else {
                loader.getInfoAboutPhoto(count, fieldForTime);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isLoading) {
            //Если во время загрузки фото произошел переход в состояние stopped, то загрузка осталанвливается.
            loader.stopLoading();
            continueLoading = true;
        }
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
                if (!isLoading && totalItemCount - 1 == lastVisibleItem && photos.size() > 0) {
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        loader.stopLoading();
        if (photos.size() > 1) {
            if (photos.get(getLastIndexOfPhotos()) == null) {
                photos.remove(getLastIndexOfPhotos());
            }
            photosCache.setLastPhotoByTime(typeOfPhotos, lastPhotoByTime);
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
        if (expectPhotos == errorPhotos.size()) { //Удаляем информацию о фотографиях, которые не смогли загрузить
            if (getLastIndexOfPhotos() == errorPhotos.size()) { //Если все фото не смогли загрузиться, то показываем notificationOfError.
                notificationOfError.setVisibility(View.VISIBLE);
            }
            photos.remove(getLastIndexOfPhotos());
            if (expectPhotos != 0) {
                photos.removeAll(errorPhotos);
                errorPhotos.clear();
                expectPhotos = 0;
            }
            adapter.notifyDataSetChanged();
            isLoading = false;
        }
    }

    @Override
    public void onSuccessLoadInfoAboutPhoto(List<PhotoEntity> photos) {
        //При запросе новых фотографий (при пролиставнии вниз), так же возращается фотография от которой строился
        //запрос, так как она уже отображена на эране, удаляем её.
        if (this.photos.size() > 1) {
            photos.remove(0);
        }
        //Убираем индикатор загрузки, если лист получился пустым.
        if (photos.isEmpty()) {
            int i = getLastIndexOfPhotos();
            this.photos.remove(i);
            adapter.notifyItemRemoved(i);
            isLoading = false;
        } else {
            expectPhotos = photos.size();
            lastPhotoByTime = photos.get(expectPhotos - 1);
            //Елси есть новые фото, то сортируем их в списке для более красивого отображения на экране.
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
            notificationOfError.setVisibility(View.VISIBLE);
        }
        int i = getLastIndexOfPhotos();
        isLoading = false;
        photos.remove(i);
        adapter.notifyItemRemoved(i);
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