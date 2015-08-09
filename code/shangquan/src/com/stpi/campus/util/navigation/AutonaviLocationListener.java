package com.stpi.campus.util.navigation;

/**
 * Created by Administrator on 2014/8/16.
 */

import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;

public class AutonaviLocationListener implements AMapLocationListener {
    private Messenger messenger;
    private int serviceMsgWhat;

    public AutonaviLocationListener(Messenger _messenger, int serviceMsgWhat) {
        this.messenger = _messenger;
        this.serviceMsgWhat = serviceMsgWhat;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location != null) {

            Message msg = Message.obtain(null, serviceMsgWhat, location);
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

}

