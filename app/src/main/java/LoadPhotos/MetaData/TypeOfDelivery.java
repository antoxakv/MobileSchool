package LoadPhotos.MetaData;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static LoadPhotos.MetaData.ValueTypeOfDelivery.*;

@StringDef({UPDATED, RUPDATED, PUBLISHED, RPUBLISHED, CREATED, RCREATED, PODDATE, RPODDAT})
@Retention(RetentionPolicy.SOURCE)
public @interface TypeOfDelivery {
}