package com.drifty.lookatphotos.LoadPhotos;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueValley {
    private static RequestQueue instance;

    public static RequestQueue getInstance(Context context) {
        if(instance == null){
            instance = Volley.newRequestQueue(context);
        }
        return instance;
    }

    public static RequestQueue getInstance() {
        return instance;
    }
}
