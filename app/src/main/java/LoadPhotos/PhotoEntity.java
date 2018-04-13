package LoadPhotos;

import android.graphics.Bitmap;

public class PhotoEntity {
    private String id;
    private String author;
    private String uid;
    private String portraitIconUrl;
    private String landscapeIconUrl;
    private String origUrl;
    private String time;

    private Bitmap portraitIcon;
    private Bitmap landscapeIcon;
    private Bitmap orig;

    public PhotoEntity(String id, String author, String uid, String portraitIconUrl, String landscapeIconUrl, String origUrl, String time) {
        this.id = id;
        this.author = author;
        this.uid = uid;
        this.portraitIconUrl = portraitIconUrl;
        this.landscapeIconUrl = landscapeIconUrl;
        this.origUrl = origUrl;
        this.time = time;
    }

    public void setPortraitIcon(Bitmap portraitIcon) {
        this.portraitIcon = portraitIcon;
    }

    public void setLandscapeIcon(Bitmap landscapeIcon) {
        this.landscapeIcon = landscapeIcon;
    }

    public void setOrig(Bitmap orig) {
        this.orig = orig;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getUid() {
        return uid;
    }

    public String getPortraitIconUrl() {
        return portraitIconUrl;
    }

    public String getLandscapeIconUrl() {
        return landscapeIconUrl;
    }

    public String getOrigUrl() {
        return origUrl;
    }

    public Bitmap getPortraitIcon() {
        return portraitIcon;
    }

    public Bitmap getLandscapeIcon() {
        return landscapeIcon;
    }

    public Bitmap getOrig() {
        return orig;
    }

    public String getTime() {
        return time;
    }
}
