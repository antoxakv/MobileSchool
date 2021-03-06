package com.drifty.lookatphotos.LoadPhotos.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

//Класс отвечает за поиск наиболее подходящего размера фотографии в горизонтальной и вертикальной
//ориентации экрана, так же находит подходящий размер для показа в ShowPhoto.
public class CalculatorSizeOfPhoto {
    private int widthScreen;
    private int heightScreen;
    private int countPhotoInLine;

    private String typeOfSizeForLandscape;
    private String typeOfSizeForPortrait;
    private String maxSizeForScreen;
    private String maxSizeInImg;
    private int height;

    public CalculatorSizeOfPhoto(int widthScreen, int heightScreen, int countPhotoInLine) {
        this.widthScreen = widthScreen;
        this.heightScreen = heightScreen;
        this.countPhotoInLine = countPhotoInLine;
    }


    public void initProperSizeOfPhotoForScreen(JSONObject img) throws JSONException {
        Iterator<String> iterator = img.keys();
        int possibleSizeForPortrait = widthScreen / countPhotoInLine;
        int possibleSizeForLandscape = heightScreen / countPhotoInLine;
        int portraitPx = 0;
        int landscapePx = 0;
        int maxSizeWidth = 0;
        int maxSizeHeight = 0;
        boolean isFind = false;
        typeOfSizeForLandscape = null;
        typeOfSizeForPortrait = null;
        maxSizeForScreen = null;
        maxSizeInImg = null;
        while (iterator.hasNext()) {
            String size = iterator.next();
            JSONObject obj = img.getJSONObject(size);
            int width = obj.getInt("width");
            int height = obj.getInt("height");
            //Поиск фото для вертикального отображения в TableOfPhotos.
            if (portraitPx < width && width <= possibleSizeForPortrait) {
                portraitPx = width;
                this.height = height;
                typeOfSizeForPortrait = size;
            }
            //Поиск фото для горизонтального отображения в TableOfPhotos.
            if (landscapePx < width && width <= possibleSizeForLandscape) {
                landscapePx = width;
                typeOfSizeForLandscape = size;
            }
            if (height > width) {
                //Если фотография портретная, то поиск первого размера фото,
                //который больше по высоте, чем высота экрана.
                if (heightScreen <= height && !isFind) {
                    maxSizeForScreen = size;
                    isFind = true;
                }
            } else {
                //В остальных случаях поиск первого размера фото,
                //который больше по ширине, чем ширина экрана.
                if (heightScreen <= width && !isFind) {
                    maxSizeForScreen = size;
                    isFind = true;
                }
            }
            if (maxSizeWidth <= width && maxSizeHeight <= height) {
                maxSizeWidth = width;
                maxSizeHeight = height;
                maxSizeInImg = size;
            }
        }
    }

    public String getTypeOfSizeForLandscape() {
        return typeOfSizeForLandscape;
    }

    public String getTypeOfSizeForPortrait() {
        return typeOfSizeForPortrait;
    }

    public String getMaxSize() {
        return maxSizeForScreen == null ? maxSizeInImg : maxSizeForScreen;
    }

    public int getHeight() {
        return height;
    }
}
