package com.drifty.lookatphotos.LoadPhotos.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class CalculatorSizeOfPhoto {
    private int widthScreen;
    private int heightScreen;
    private int countPhotoInLine;

    private String typeOfSizeForLandscape;
    private String typeOfSizeForPortrait;
    private String minSize;
    private String maxSize;
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
        int minSizeWidth = 0;
        int minSizeHeight = 0;
        int maxSizeWidth = 0;
        int maxSizeHeight = 0;
        while (iterator.hasNext()) {
            String size = iterator.next();
            JSONObject obj = img.getJSONObject(size);
            int width = obj.getInt("width");
            int height = obj.getInt("height");
            if (portraitPx < width && width <= possibleSizeForPortrait) {
                portraitPx = width;
                this.height = height;
                typeOfSizeForPortrait = size;
            }
            if (landscapePx < width && width <= possibleSizeForLandscape) {
                landscapePx = width;
                typeOfSizeForLandscape = size;
            }
            if ((minSizeWidth >= width && minSizeHeight >= height) || minSizeWidth == 0 || minSizeHeight == 0) {
                minSizeWidth = width;
                minSizeHeight = height;
                minSize = size;
            }
            if (maxSizeWidth <= width && maxSizeHeight <= height) {
                maxSizeWidth = width;
                maxSizeHeight = height;
                maxSize = size;
            }
        }
    }

    public String getTypeOfSizeForLandscape() {
        return typeOfSizeForLandscape;
    }

    public String getTypeOfSizeForPortrait() {
        return typeOfSizeForPortrait;
    }

    public String getMinSize() {
        return minSize;
    }

    public String getMaxSize() {
        return maxSize;
    }

    public int getHeight() {
        return height;
    }
}
