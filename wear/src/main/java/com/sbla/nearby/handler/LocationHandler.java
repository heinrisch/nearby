package com.sbla.nearby.handler;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import rx.Observable;
import rx.subjects.PublishSubject;


public class LocationHandler {

    private static PublishSubject<Location> mSubject = PublishSubject.create();
    private static LocationClient mLocationClient;

    public static Observable<Location> getLocation(Context context) {

        if (mSubject == null) {
            mSubject = PublishSubject.create();
        }

        if (mLocationClient == null) {
            mLocationClient = new LocationClient(context,
                    getConnectionCallbacksHandler(),
                    getOnConnectionFailedListener()
            );
            mLocationClient.connect();
        } else {
            mSubject.onNext(mLocationClient.getLastLocation());
        }

        Log.e("Test", "Trying to get location");
        return mSubject.asObservable();
    }

    private static GooglePlayServicesClient.ConnectionCallbacks getConnectionCallbacksHandler() {
        return new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.e("Google play services", "Connected");
                Location location = mLocationClient.getLastLocation();
                if(location == null){
                    mLocationClient.requestLocationUpdates(LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY), new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            mSubject.onNext(mLocationClient.getLastLocation());
                            mLocationClient.removeLocationUpdates(this);
                        }
                    });
                }else {
                    mSubject.onNext(mLocationClient.getLastLocation());
                }
            }

            @Override
            public void onDisconnected() {
                fail();
            }
        };
    }

    private static GooglePlayServicesClient.OnConnectionFailedListener getOnConnectionFailedListener() {
        return new GooglePlayServicesClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                fail();
            }
        };
    }


    private static void fail() {
        mSubject.onError(new Throwable("Failed to get location"));
    }
}
