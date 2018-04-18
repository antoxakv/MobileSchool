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
import com.drifty.lookatphotos.R;

import java.util.ArrayList;
import java.util.List;

import LoadPhotos.MetaData.TypeFieldForTime;
import LoadPhotos.MetaData.TypeOfDelivery;
import LoadPhotos.MetaData.TypeOfPhotos;
import LoadPhotos.RequestQueueValley;

public class MainActivity extends AppCompatActivity {

    private TableOfPhotos newInterestingPhotos;
    private TableOfPhotos popularPhotos;

    private int topPadding = 2;
    private int countPhotoInLine = 2;
    private int count = 20;
    private Bundle screenConfig = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RequestQueueValley.getInstance(this);
        initSizeScreen();
        screenConfig.putInt(BundleFields.COUNT_PHOTO_IN_LINE, countPhotoInLine);
        screenConfig.putInt(BundleFields.COUNT, count);
        newInterestingPhotos = newTablePhotos(TypeOfDelivery.UPDATED, TypeOfPhotos.NEW_INTERESTING_PHOTOS, TypeFieldForTime.UPDATED);
        popularPhotos = newTablePhotos(TypeOfDelivery.UPDATED, TypeOfPhotos.POPULAR_PHOTOS, TypeFieldForTime.UPDATED);

        ViewPager pager = findViewById(R.id.pager);
        final List<TableOfPhotos> fragments = new ArrayList<>(2);
        fragments.add(newInterestingPhotos);
        fragments.add(popularPhotos);
        final List<String> titleOfFragments = new ArrayList<>(2);
        titleOfFragments.add(getResources().getString(R.string.interesting));
        titleOfFragments.add(getResources().getString(R.string.popular));
        FragmentPagerAdapter fpa = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titleOfFragments.get(position);
            }
        };
        pager.setAdapter(fpa);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

    }

    private TableOfPhotos newTablePhotos(String typeOfDelivery, String typeOfPhotos, String fieldForTime) {
        TableOfPhotos top = new TableOfPhotos();
        Bundle bundle = (Bundle) screenConfig.clone();
        bundle.putString(BundleFields.TYPE_OF_DELIVERY, typeOfDelivery);
        bundle.putString(BundleFields.TYPE_OF_PHOTOS, typeOfPhotos);
        bundle.putString(BundleFields.FIELD_FOR_TIME, fieldForTime);
        top.setArguments(bundle);
        return top;
    }

    private void initSizeScreen() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        topPadding *= getResources().getDisplayMetrics().density;
        screenConfig.putInt(BundleFields.TOP_PADDING, topPadding);
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                screenConfig.putBoolean(BundleFields.IS_PORTRAIT, true);
                screenConfig.putInt(BundleFields.WIDTH_SCREEN, size.x);
                screenConfig.putInt(BundleFields.HEIGHT_SCREEN, size.y);
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                screenConfig.putBoolean(BundleFields.IS_PORTRAIT, false);
                screenConfig.putInt(BundleFields.WIDTH_SCREEN, size.y);
                screenConfig.putInt(BundleFields.HEIGHT_SCREEN, size.x);
        }
    }

}
