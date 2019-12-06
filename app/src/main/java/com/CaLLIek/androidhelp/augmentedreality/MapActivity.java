package com.CaLLIek.androidhelp.augmentedreality;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;

import com.CaLLIek.androidhelp.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity {

    public String TAG = this.getClass().getName();
    private boolean Debug = true;

    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_activity_map);

        createMapView();
        addMarker();
        setTitle(getString(R.string.p_name));
    }

    private void createMapView()
    {
        try{
            if(googleMap == null)
            {
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();

                if(googleMap == null)
                {
                    Toast.makeText(this,"ErrorCreating map", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (NullPointerException ex)
        {
            if(Debug)Log.e(TAG, ex.toString());
        }
    }

    private void addMarker()
    {
        double lat  = CameraViewActivity.TARGET_LATITUDE;
        double lng = CameraViewActivity.TARGET_LONGITUDE;

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat,lng))
                .zoom(15)
                .build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        googleMap.animateCamera(cameraUpdate);

        if(googleMap != null)
        {
            googleMap.addMarker(new MarkerOptions()
            .position(new LatLng(lat,lng))
            .title(getString(R.string.p_name))
                            .draggable(true)
            );
        }
    }
}
