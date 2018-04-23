package com.drifty.lookatphotos.ApplicationContext;

import android.app.Application;
import android.graphics.Bitmap;

import com.drifty.lookatphotos.LoadPhotos.PhotoEntity;

import java.util.HashMap;
import java.util.List;

public class PhotosCache extends Application {
    private HashMap<String, List<PhotoEntity>> map = new HashMap<>();
    private Bitmap currentPhoto;

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

}
