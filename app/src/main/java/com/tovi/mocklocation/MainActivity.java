package com.tovi.mocklocation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private double mLongitude = 40.052462;
    private double mLatitude = 119.29064;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MockLocation.pushLocation(this, mLongitude, mLatitude);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MockLocation.unenableTestProvider();
    }
}
