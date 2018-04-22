package com.drifty.lookatphotos.ApplicationContext;

import android.app.Application;

import com.drifty.lookatphotos.LoadPhotos.PhotoEntity;

import java.util.HashMap;
import java.util.List;

public class PhotosCache extends Application {
    private HashMap<String, List<PhotoEntity>> map = new HashMap<>();

    public void setListPhotoEntity(String type, List<PhotoEntity> list) {
        map.put(type, list);
    }

    public List<PhotoEntity> getListPhotoEntity(String type) {
        return map.get(type);
    }

}
