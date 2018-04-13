package LoadPhotos.MetaData;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static LoadPhotos.MetaData.ValueTypeOfPhotos.*;

@StringDef({NEW_INTERESTING_PHOTOS, POPULAR_PHOTOS, PHOTOS_OF_DAY})
@Retention(RetentionPolicy.SOURCE)
public @interface TypeOfPhotos {
}
