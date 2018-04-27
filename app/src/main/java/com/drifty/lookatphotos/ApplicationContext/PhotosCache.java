package com.drifty.lookatphotos.ApplicationContext;

import android.app.Application;
import android.graphics.Bitmap;

import com.drifty.lookatphotos.LoadPhotos.Tools.PhotoEntity;

import java.util.HashMap;
import java.util.List;

public class PhotosCache extends Application {
    private HashMap<String, List<PhotoEntity>> map = new HashMap<>();
    private Bitmap currentPhoto;
    private PhotoEntity lastPhotoByTime;

    public Bitmap getCurrentPhoto() {
        return currentPhoto;
    }

    public void setCurrentPhoto(Bitmap currentPhoto) {
        this.currentPhoto = currentPhoto;
    }

    public void setListPhotoEntity(String type, List<PhotoEntity> list) {
        map.put(type, list);
    }

    public List<PhotoEntity> getListPhotoEntity(String type) {
        return map.get(type);
    }

    public PhotoEntity getLastPhotoByTime() {
        return lastPhotoByTime;
    }

    public void setLastPhotoByTime(PhotoEntity lastPhotoByTime) {
        this.lastPhotoByTime = lastPhotoByTime;
    }
}
