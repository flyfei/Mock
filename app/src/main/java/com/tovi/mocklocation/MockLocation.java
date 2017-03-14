package com.tovi.mocklocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * @author <a href='mailto:zhaotengfei9@gmail.com'>Tengfei Zhao</a>
 *         <p>
 *         all permission
 *         <uses-permission android:name="android.permission.ACCESS_MOCK_LOCATION"/>
 *         <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 *         <uses-permission android:name="android.permission.WRITE_SETTINGS" />
 *         <uses-permission android:name="android.permission.WRITE_SECURE_SETTINGS" />
 */

public class MockLocation {
    private static final String TAG = "MockLocation";
    private static LocationManager mLocationManager;
    private static Context mContext;
    private static String mMockProviderName = LocationManager.GPS_PROVIDER;

    public static void pushLocation(Context context, double longitude, double latitude) {
        mContext = context.getApplicationContext();
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        enableTestProvider();
        setLocation(mContext, longitude, latitude);//设置模拟位置信息，也可以建一个thread不断设置虚拟位置信息。
    }

    private static void enableTestProvider() {
        ContentResolver res = mContext.getContentResolver();
        //获取gps的状态，false为关闭，true为开启。
        boolean gps_enable = Settings.Secure.isLocationProviderEnabled(
                res, android.location.LocationManager.GPS_PROVIDER);
//        if (gps_enable) {
//            //关闭gps
//            Settings.Secure.setLocationProviderEnabled(res, LocationManager.GPS_PROVIDER, false);
//        }
        //获取“允许模拟地点”的状态，0为不允许，1为允许。
//        int mock_enable = Settings.Secure.getInt(
//                res, Settings.Secure.ALLOW_MOCK_LOCATION, 0);
//        if (mock_enable == 0) {
//            try {
//                //开启 允许模拟地点
//                Settings.Secure.putInt(res, Settings.Secure.ALLOW_MOCK_LOCATION, 1);
//            } catch (Exception e) {
//                Log.e(TAG, "write error", e);
//            }
//        }
        mLocationManager.addTestProvider(mMockProviderName,
                "requiresNetwork" == "", "requiresSatellite" == "",
                "requiresCell" == "", "hasMonetaryCost" == "",
                "supportsAltitude" == "", "supportsSpeed" == "",
                "supportsBearing" == "supportsBearing",
                Criteria.POWER_LOW,
                Criteria.ACCURACY_FINE);
        mLocationManager.setTestProviderEnabled(mMockProviderName, true);
    }

    @SuppressLint("NewApi")
    private static void setLocation(Context context, double longitude, double latitude) {
        Log.d(TAG, "setLocation");
        Location loc = new Location(mMockProviderName);//这里是模拟的gps位置信息，当然也可以设置network位置信息了。
        loc.setAccuracy(Criteria.ACCURACY_FINE);
        loc.setTime(System.currentTimeMillis());//设置当前时间
        loc.setLongitude(longitude);           //设置经度
        loc.setLatitude(latitude);           //设置纬度
        loc.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        mLocationManager.setTestProviderLocation(mMockProviderName, loc);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "setLocation: not find ACCESS_FINE_LOCATION Or ACCESS_COARSE_LOCATION");
            return;
        }
        System.out.println(mLocationManager.getLastKnownLocation(mMockProviderName).getLatitude() + "  " + mLocationManager.getLastKnownLocation(mMockProviderName).getLongitude());
    }

    public static void unenableTestProvider() {
        int mock_enable = Settings.Secure.getInt(
                mContext.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0);
        if (mock_enable == 0) return;
        try {
            mLocationManager.clearTestProviderEnabled(mMockProviderName);
            mLocationManager.removeTestProvider(mMockProviderName);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
//        try {
//            //关闭 允许模拟地点
//            Settings.Secure.putInt(mContext.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0);
//        } catch (Exception e) {
//            Log.e(TAG, "write error", e);
//        }
    }
}
