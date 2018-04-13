package LoadPhotos;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import LoadPhotos.MetaData.*;

public class LoaderPhotos {

    private RequestQueue rq;
    private String urlForList;
    private String url;
    private final static HashMap<String, String> headers = new HashMap<>(1);
    private CallBack cb;
    private CalculatorSizeOfPhoto csop;

    public LoaderPhotos(RequestQueue rq, @TypeOfPhotos String typeOfPhotos, CalculatorSizeOfPhoto csop, CallBack cb) {
        this.rq = rq;
        String hostAndTypeOfPhotos = "https://api-fotki.yandex.ru/api/" + typeOfPhotos;
        urlForList = hostAndTypeOfPhotos + "/{typeOfDelivery};{time},{id},{uid},/?limit={count}";
        url = hostAndTypeOfPhotos + "/?limit={count}";
        headers.put("Accept", "application/json");
        this.cb = cb;
        this.csop = csop;
    }

    public void getInfoAboutPhoto(int count) {
        createRequestForInfoAboutPhoto(replaceInUrl(count), ValueTypeOfDelivery.UPDATED);
    }

    public void getInfoAboutPhoto(@TypeOfDelivery String typeOfDelivery, @TypeOfDelivery String fieldForTime, String time, String id, String uid, int count) {
        createRequestForInfoAboutPhoto(replaceInUrlForList(typeOfDelivery, time, id, uid, count), fieldForTime);
    }

    public void getPhoto(String url, final PhotoEntity pe) {
        ImageRequest ir = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                cb.onSuccessLoadPhoto(response, pe);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cb.onFailedLoadPhoto(error.getMessage());
            }
        });
        rq.add(ir);
    }

    private void createRequestForInfoAboutPhoto(String url, @TypeOfDelivery final String fieldForTime) {
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray entries = response.getJSONArray("entries");
                    ArrayList<PhotoEntity> photos = new ArrayList<>(entries.length());
                    for (int i = 0; i < entries.length(); i++) {
                        JSONObject obj = entries.getJSONObject(i);
                        String id = obj.getString("id");
                        id = id.substring(id.lastIndexOf(":") + 1);
                        JSONObject authors = obj.getJSONArray("authors").getJSONObject(0);
                        String author = authors.getString("name");
                        String uid = authors.getString("uid");
                        JSONObject img = obj.getJSONObject("img");
                        String href = "href";
                        csop.initProperSizeOfPhotoForScreen(img);
                        String portraitIconUrl = getIconUrl(csop.getTypeOfSizeForPortrait(), img, href);
                        String landscapeIconUrl = getIconUrl(csop.getTypeOfSizeForLandscape(), img, href);
                        String orig = img.getJSONObject(csop.getMaxSize()).getString(href);
                        String time = obj.getString(fieldForTime);
                        photos.add(new PhotoEntity(id, author, uid, portraitIconUrl, landscapeIconUrl, orig, time));
                    }
                    cb.onSuccessLoadInfoAboutPhoto(photos);
                } catch (JSONException e) {
                    cb.onFailedLoadInfoAboutPhoto(e.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cb.onFailedLoadInfoAboutPhoto(error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };
        rq.add(jor);
    }

    private String getIconUrl(String typeOfSize, JSONObject img, String href) throws JSONException {
        String iconUrl;
        if (typeOfSize == null) {
            iconUrl = img.getJSONObject(csop.getMinSize()).getString(href);
        } else {
            iconUrl = img.getJSONObject(typeOfSize).getString(href);
        }
        return iconUrl;
    }

    private String replaceInUrlForList(@TypeOfDelivery String typeOfDelivery, @TypeOfDelivery String time, String id, String uid, int count) {
        return urlForList.replace("{typeOfDelivery}", typeOfDelivery)
                .replace("{time}", time)
                .replace("{id}", id)
                .replace("{uid}", uid)
                .replace("{count}", String.valueOf(count));
    }

    private String replaceInUrl(int count) {
        return url.replace("{count}", String.valueOf(count));
    }

    public interface CallBack {
        void onSuccessLoadPhoto(Bitmap photo, PhotoEntity pe);

        void onFailedLoadPhoto(String error);

        void onSuccessLoadInfoAboutPhoto(List<PhotoEntity> photos);

        void onFailedLoadInfoAboutPhoto(String error);
    }
}
