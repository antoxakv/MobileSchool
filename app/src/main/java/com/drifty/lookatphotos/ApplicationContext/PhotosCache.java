package com.drifty.lookatphotos.ApplicationContext;

import android.app.Application;
import android.graphics.Bitmap;

import com.drifty.lookatphotos.LoadPhotos.Tools.PhotoEntity;

import java.util.HashMap;
import java.util.List;

public class PhotosCache extends Application {
    private HashMap<String, List<PhotoEntity>> listPhotoEntities = new HashMap<>();
    private Bitmap currentPhoto;
    private HashMap<String, PhotoEntity> lastPhotoByTime = new HashMap<>();

    public Bitmap getCurrentPhoto() {
        return currentPhoto;
    }

    public void setCurrentPhoto(Bitmap currentPhoto) {
        this.currentPhoto = currentPhoto;
    }

    public void setListPhotoEntity(String type, List<PhotoEntity> list) {
        listPhotoEntities.put(type, list);
    }

    public List<PhotoEntity> getListPhotoEntity(String type) {
        return listPhotoEntities.get(type);
    }

    public void setLastPhotoByTime(String type, PhotoEntity photo) {
        lastPhotoByTime.put(type, photo);
    }

    public PhotoEntity getLastPhotosByTime(String type) {
        return lastPhotoByTime.get(type);
    }
}
