package com.drifty.lookatphotos.ApplicationContext;

import android.app.Application;
import android.graphics.Bitmap;

import com.drifty.lookatphotos.LoadPhotos.Tools.PhotoEntity;

import java.util.HashMap;
import java.util.List;

//Хранятся данные, которые необходимо сохранить при изменении конфигурации.
public class PhotosCache extends Application {
    //В map'ах хранятся данные каждого экземпляра TableOfPhotos.
    //Ключом выступает значение, которое определяет какой тип фото показывает TableOfPhotos.
    private HashMap<String, List<PhotoEntity>> listPhotoEntities = new HashMap<>();
    private HashMap<String, PhotoEntity> lastPhotoByTime = new HashMap<>();
    //Фото для показа в ShowPhoto.
    private Bitmap currentPhoto;

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
