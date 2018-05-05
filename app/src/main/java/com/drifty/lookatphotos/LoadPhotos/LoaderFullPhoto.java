package com.drifty.lookatphotos.LoadPhotos;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

//Загрузчик фотографий для ShowPhoto.
public class LoaderFullPhoto {
    private RequestQueue rq;
    private LoaderFullPhoto.CallBack cb;

    private static final String ORIG_PHOTO = "ORIG_PHOTO";

    public LoaderFullPhoto(RequestQueue rq, LoaderFullPhoto.CallBack cb) {
        this.rq = rq;
        this.cb = cb;
    }

    public void getPhoto(String url) {
        ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                cb.onSuccessLoadPhoto(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cb.onFailedLoadPhoto();
            }
        });

        rq.add(ir);
    }

    public void cancelLoadPhoto() {
        rq.cancelAll(ORIG_PHOTO);
    }

    public interface CallBack {
        void onSuccessLoadPhoto(Bitmap photo);

        void onFailedLoadPhoto();
    }
}
