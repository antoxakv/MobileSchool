package com.drifty.lookatphotos.Activities;

import android.graphics.Point;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;

import com.drifty.lookatphotos.Fragments.BundleFields;
import com.drifty.lookatphotos.Fragments.TableOfPhotos;
import com.drifty.lookatphotos.LoadPhotos.LoaderInfoAboutPhotos;
import com.drifty.lookatphotos.R;

import java.util.ArrayList;
import java.util.List;

import com.drifty.lookatphotos.LoadPhotos.Tools.RequestQueueValley;

/*
adb -e shell svc data disable
adb -e shell svc data enable
*/
public class MainActivity extends AppCompatActivity {

    private int countPhotoInLine = 2;
    private int count = 20;
    private int width;
    private int height;
    private boolean isPortrait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RequestQueueValley.getInstance(getApplicationContext());
        initSizeScreen();
        if (savedInstanceState != null) {
            //увеломление фрагментов(TableOfPhotos) об изменении кофигурации
            for (Fragment top : getSupportFragmentManager().getFragments()) {
                Bundle bundle = top.getArguments();
                top.setArguments(newBundleForArgs(bundle.getString(BundleFields.TYPE_OF_DELIVERY),
                        bundle.getString(BundleFields.TYPE_OF_PHOTOS),
                        bundle.getString(BundleFields.FIELD_FOR_TIME)));
            }
        }
        initViewPager();
    }

    private void initViewPager() {
        final List<String> titleOfTableOfPhotos = new ArrayList<>(2);
        titleOfTableOfPhotos.add(getResources().getString(R.string.interesting));
        titleOfTableOfPhotos.add(getResources().getString(R.string.popular));
        ViewPager pager = findViewById(R.id.pager);
        FragmentPagerAdapter fpa = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment f = null;
                switch (position) {
                    case 0:
                        f = newTablePhotos(LoaderInfoAboutPhotos.DELIVERY_AND_FIELD, LoaderInfoAboutPhotos.NEW_INTERESTING_PHOTOS, LoaderInfoAboutPhotos.DELIVERY_AND_FIELD);
                        break;
                    case 1:
                        f = newTablePhotos(LoaderInfoAboutPhotos.DELIVERY_AND_FIELD, LoaderInfoAboutPhotos.POPULAR_PHOTOS, LoaderInfoAboutPhotos.DELIVERY_AND_FIELD);
                }
                return f;
            }

            @Override
            public int getCount() {
                return titleOfTableOfPhotos.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titleOfTableOfPhotos.get(position);
            }
        };
        pager.setAdapter(fpa);
        ((TabLayout) findViewById(R.id.tabs)).setupWithViewPager(pager);
    }

    private TableOfPhotos newTablePhotos(String typeOfDelivery, String typeOfPhotos, String fieldForTime) {
        TableOfPhotos top = new TableOfPhotos();
        top.setArguments(newBundleForArgs(typeOfDelivery, typeOfPhotos, fieldForTime));
        return top;
    }

    private Bundle newBundleForArgs(String typeOfDelivery, String typeOfPhotos, String fieldForTime) {
        Bundle bundle = new Bundle();
        bundle.putInt(BundleFields.WIDTH_SCREEN, width);
        bundle.putInt(BundleFields.HEIGHT_SCREEN, height);
        bundle.putBoolean(BundleFields.IS_PORTRAIT, isPortrait);
        bundle.putInt(BundleFields.COUNT, count);
        bundle.putInt(BundleFields.COUNT_PHOTO_IN_LINE, countPhotoInLine);
        bundle.putString(BundleFields.TYPE_OF_DELIVERY, typeOfDelivery);
        bundle.putString(BundleFields.TYPE_OF_PHOTOS, typeOfPhotos);
        bundle.putString(BundleFields.FIELD_FOR_TIME, fieldForTime);
        return bundle;
    }

    private void initSizeScreen() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                isPortrait = true;
                width = size.x;
                height = size.y;
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                isPortrait = false;
                width = size.y;
                height = size.x;
        }
    }
}
