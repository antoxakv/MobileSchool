package com.drifty.lookatphotos.LoadPhotos;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.drifty.lookatphotos.LoadPhotos.Tools.CalculatorSizeOfPhoto;
import com.drifty.lookatphotos.LoadPhotos.Tools.PhotoEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoaderInfoAboutPhotos {

    public static final String NEW_INTERESTING_PHOTOS = "recent";
    public static final String POPULAR_PHOTOS = "top";
    public static final String DELIVERY_AND_FIELD = "updated";

    private RequestQueue rq;
    private String urlForList;
    private String url;
    private final static HashMap<String, String> headers = new HashMap<>(1);
    private CallBack cb;
    private CalculatorSizeOfPhoto csop;
    private String typeOfPhotos;

    public LoaderInfoAboutPhotos(RequestQueue rq, String typeOfPhotos, CalculatorSizeOfPhoto csop, CallBack cb) {
        this.rq = rq;
        //Происходит построение запросов-шаблонов.
        String hostAndTypeOfPhotos = "https://api-fotki.yandex.ru/api/" + typeOfPhotos;
        //Запрос для выдачи информации о фотографиях постранично.
        urlForList = hostAndTypeOfPhotos + "/{typeOfDelivery};{time},{id},{uid}/?limit={count}";
        //Запрос для выдачи информации о фотографиях, выдача ограничена только кол-ом фото в выдаче.
        url = hostAndTypeOfPhotos + "/?limit={count}";
        headers.put("Accept", "application/json");
        this.cb = cb;
        this.csop = csop;
        this.typeOfPhotos = typeOfPhotos;
    }

    public void getInfoAboutPhoto(int count, String fieldForTime) {
        createRequestForInfoAboutPhoto(replaceInUrl(count), fieldForTime);
    }

    public void getInfoAboutPhoto(String typeOfDelivery, String fieldForTime, String time, String id, String uid, int count) {
        createRequestForInfoAboutPhoto(replaceInUrlForList(typeOfDelivery, time, id, uid, count), fieldForTime);
    }

    //Загрузка иконок для TableOfPhotos.
    public void getPhoto(String url, final PhotoEntity pe) {
        ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                cb.onSuccessLoadPhoto(response, pe);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cb.onFailedLoadPhoto(pe);
            }
        });
        ir.setTag(typeOfPhotos);
        rq.add(ir);
    }

    private void createRequestForInfoAboutPhoto(String url, final String fieldForTime) {
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray entries = response.getJSONArray("entries");
                    //Ответ в формате json преобразуется в лист photos.
                    ArrayList<PhotoEntity> photos = new ArrayList<>(entries.length());
                    for (int i = 0; i < entries.length(); i++) {
                        JSONObject obj = entries.getJSONObject(i);
                        String id = obj.getString("id");
                        id = id.substring(id.lastIndexOf(":") + 1);
                        JSONObject authors = obj.getJSONArray("authors").getJSONObject(0);
                        String uid = authors.getString("uid");
                        JSONObject img = obj.getJSONObject("img");
                        String href = "href";
                        //Вызывается экземпляр класса CalculatorSizeOfPhoto для расчета подходящего размера иконок и фотографии.
                        csop.initProperSizeOfPhotoForScreen(img);
                        String portraitIconUrl = img.getJSONObject(csop.getTypeOfSizeForPortrait()).getString(href);
                        String landscapeIconUrl = img.getJSONObject(csop.getTypeOfSizeForLandscape()).getString(href);
                        String orig = img.getJSONObject(csop.getMaxSize()).getString(href);
                        String time = obj.getString(fieldForTime);
                        photos.add(new PhotoEntity(id, uid, portraitIconUrl, landscapeIconUrl, orig, time, csop.getHeight()));
                    }
                    cb.onSuccessLoadInfoAboutPhoto(photos);
                } catch (JSONException e) {
                    cb.onFailedLoadInfoAboutPhoto();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cb.onFailedLoadInfoAboutPhoto();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };
        jor.setTag(typeOfPhotos);
        rq.add(jor);
    }

    public void stopLoading() {
        rq.cancelAll(typeOfPhotos);
    }

    private String replaceInUrlForList(String typeOfDelivery, String time, String id, String uid, int count) {
        return urlForList.replace("{typeOfDelivery}", typeOfDelivery)
                .replace("{time}", time)
                .replace("{id}", id)
                .replace("{uid}", uid)
                .replace("{count}", String.valueOf(count));
    }

    private String replaceInUrl(int count) {
        return url.replace("{count}", String.valueOf(count));
    }

    //CallBack реализует TableOfPhotos, куда происходит возвращение ответа.
    public interface CallBack {
        void onSuccessLoadPhoto(Bitmap photo, PhotoEntity pe);

        void onFailedLoadPhoto(PhotoEntity pe);

        void onSuccessLoadInfoAboutPhoto(List<PhotoEntity> photos);

        void onFailedLoadInfoAboutPhoto();
    }
}