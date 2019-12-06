package com.CaLLIek.androidhelp.augmentedreality;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


/**
 * Created by Android Studio.
 * User: CaLLIek
 * Date: 04.12.2019
 * Time: 13:29
 */

public class MyCurrentLocation implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener
{

    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private OnLocationChangedListener onLocationChangedListener;

    public MyCurrentLocation(OnLocationChangedListener onLocationChangedListener)
    {
        this.onLocationChangedListener = onLocationChangedListener;
    }

    protected synchronized void buildGoogleApiClient(Context context)
    {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10*1000)
                .setFastestInterval(1*1000);
    }

    public void start()
    {
        googleApiClient.connect();
    }

    public void stop()
    {
        googleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(lastLocation != null)
            onLocationChangedListener.onLocationChanged(lastLocation);
    }
/*
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    */

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest, this);
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(lastLocation!=null)
        {
            onLocationChangedListener.onLocationChanged(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("MyApp","Location services connection failed with code "+ connectionResult.getErrorCode());
    }

}
