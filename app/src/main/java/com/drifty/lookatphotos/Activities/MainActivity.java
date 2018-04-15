package com.drifty.lookatphotos.Activities;

import android.app.FragmentTransaction;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.Surface;

import com.drifty.lookatphotos.Fragments.TableOfPhotos;
import com.drifty.lookatphotos.R;

import LoadPhotos.RequestQueueValley;

public class MainActivity extends AppCompatActivity {

    private FragmentTransaction ft;
    private BottomNavigationView switchFragments;
    private TableOfPhotos newInterestingPhotos;

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
        initSwitchFragments();
        screenConfig.putInt("countPhotoInLine", countPhotoInLine);
        screenConfig.putInt("count", count);
        newInterestingPhotos = new TableOfPhotos();
        newInterestingPhotos.setArguments(screenConfig);
        ft = getFragmentManager().beginTransaction();
        ft.add(R.id.cont_frag, newInterestingPhotos);
        ft.show(newInterestingPhotos);
        ft.commit();
    }

    private void initSwitchFragments() {
        switchFragments = findViewById(R.id.navigation);
        switchFragments.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                ft = getFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.newInterestingPhotoFrag:
                        ft.show(newInterestingPhotos);
                        break;
                    case R.id.popularPhotoFrag:
                        ft.hide(newInterestingPhotos);
                        break;
                    case R.id.photoOfDayFrag:
                        ft.hide(newInterestingPhotos);
                        break;
                }
                ft.commit();
                return true;
            }
        });
    }

    private void initSizeScreen() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        topPadding *= getResources().getDisplayMetrics().density;
        screenConfig.putInt("topPadding", topPadding);
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                screenConfig.putBoolean("isPortrait", true);
                screenConfig.putInt("widthScreen", size.x);
                screenConfig.putInt("heightScreen", size.y);
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                screenConfig.putBoolean("isPortrait", false);
                screenConfig.putInt("widthScreen", size.y);
                screenConfig.putInt("heightScreen", size.x);
        }
    }

}
