package com.CaLLIek.androidhelp.augmentedreality;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.CaLLIek.androidhelp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraViewActivity extends AppCompatActivity implements
        SurfaceHolder.Callback, OnLocationChangedListener,OnAzimuthChangedListener,
        View.OnClickListener {

    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private boolean isCameraViewOn = false;
    private Pikachu pikachu;

    private double azimuthReal = 0;
    private double azimiuthTheoretical = 0;
    private static double DISTANCE_ACCURACY = 5;
    private static double AZIMUTH_ACCURACY = 10;

    private double latitude = 0;
    private double longitude = 0;
    public static double TARGET_LATITUDE = 46.95484001;
    public static double TARGET_LONGITUDE = 31.99046939;

    private MyCurrentAzimuth myCurrentAzimuth;
    private MyCurrentLocation myCurrentLocation;

    TextView descriptionTextView;
    ImageView pointerIcon;
    Button btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_activity_camera_view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        pointerIcon = findViewById(R.id.icon);

        setupListeners();
        setupLayout();
        setAugmentedRealityPoint();
    }

    private void setAugmentedRealityPoint()
    {
        pikachu = new Pikachu(getString(R.string.p_name),TARGET_LATITUDE,TARGET_LONGITUDE);
    }

    public double calculateDistance()
    {
        double dX = pikachu.getLatitude() - latitude;
        double dY = pikachu.getLongitude() - longitude;

        double distance = (Math.sqrt(Math.pow(dX,2) + Math.pow(dY,2)) *100000);
        return distance;
    }

    public double calculateTheoreticalAzimuth()
    {
        double dX = pikachu.getLatitude() - latitude;
        double dY = pikachu.getLongitude() - longitude;

        double phiAngle;
        double tanPhi;
        double azimuth = 0;
        tanPhi = Math.abs(dY / dX);
        phiAngle = Math.atan(tanPhi);
        phiAngle = Math.toDegrees(phiAngle);

        if(dX>0 && dY > 0)
        {
            return azimuth = phiAngle;
        }
        else
            if(dX<0 && dY > 0)
            {
                return azimuth = 180 - phiAngle;
            }
            else
                if(dX<0 && dY<0)
                {
                    return azimuth = 180 + phiAngle;
                }
                else
                    if(dX>0 && dY<0)
                    {
                        return azimuth = 360-phiAngle;
                    }
        return phiAngle;
    }

    private List<Double> calculateAzimuthAccuracy(double azimuth)
    {
        double minAngle = azimuth - AZIMUTH_ACCURACY;
        double maxAngle = azimuth + AZIMUTH_ACCURACY;

        List<Double> minMax = new ArrayList<>();

        if(minAngle < 0)
            minAngle +=360;

        if(maxAngle >= 360)
            maxAngle -=360;

        minMax.clear();
        minMax.add(minAngle);
        minMax.add(maxAngle);

        return minMax;
    }

    private boolean isBetween(double minAngle, double maxAngle, double azimuth)
    {
        if(minAngle > maxAngle)
        {
            if(isBetween(0,maxAngle,azimuth) && isBetween(minAngle,360,azimuth))
                return true;
        }
        else
        {
            if(azimuth > minAngle && azimuth < maxAngle)
                return true;
        }
        return false;
    }

    private void updateDescription()
    {
        long distance = (long) calculateDistance();
        int tAzimuth = (int)azimiuthTheoretical;
        int rAzimuth = (int)azimuthReal;

        String text = pikachu.getName()
                + " location:"
                + "\n latitude: " + TARGET_LATITUDE + " longitude: "+ TARGET_LONGITUDE
                +"\n Current location: "
                +"\n latitude: " + latitude + " longitude "+ longitude
                +"\n"
                +"\n Target azimuth: "+ tAzimuth
                +"\n Current azimuth: "+ rAzimuth
                +"\n Distance: "+ distance;
        descriptionTextView.setText(text);
    }

    @Override
    public void onAzimuthChanged(float azimuthChangedFrom, float azimuthChangedTo)
    {
        azimuthReal = azimuthChangedTo;
        azimiuthTheoretical = calculateTheoreticalAzimuth();
        int distance = (int)calculateDistance();

        double minAngle = calculateAzimuthAccuracy(azimiuthTheoretical).get(0);
        double maxAngle = calculateAzimuthAccuracy(azimiuthTheoretical).get(0);

        if((isBetween(minAngle,maxAngle,azimuthReal)) && distance <= DISTANCE_ACCURACY)
        {
            pointerIcon.setVisibility(View.VISIBLE);
        }
        else
        {
            pointerIcon.setVisibility(View.INVISIBLE);
        }

        updateDescription();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        azimiuthTheoretical = calculateTheoreticalAzimuth();
        int distance = (int) calculateDistance();

        Toast.makeText(this,"latitude: "+location.getLatitude()+" longitude: "
        +location.getLongitude(),Toast.LENGTH_SHORT).show();

        if(azimuthReal == 0)
        {
            if(distance <= DISTANCE_ACCURACY)
            {
                pointerIcon.setVisibility(View.VISIBLE);
            }
            else
            {
                pointerIcon.setVisibility(View.INVISIBLE);
            }
        }
        updateDescription();
    }

    @Override
    protected void onStop()
    {
        myCurrentAzimuth.stop();
        myCurrentLocation.stop();
        super.onStop();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        myCurrentAzimuth.start();
        myCurrentLocation.start();
    }

    private void setupListeners()
    {
        myCurrentLocation = new MyCurrentLocation(this);
        myCurrentLocation.buildGoogleApiClient(this);
        myCurrentLocation.start();

        myCurrentAzimuth = new MyCurrentAzimuth(this,this);
        myCurrentAzimuth.start();
    }

    private void setupLayout()
    {
        descriptionTextView = findViewById(R.id.cameraTextView);
        btnMap = findViewById(R.id.btnMap);
        btnMap.setVisibility(View.VISIBLE);
        btnMap.setOnClickListener(this);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        SurfaceView surfaceView = findViewById(R.id.cameraview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        if(isCameraViewOn)
        {
            camera.stopPreview();
            isCameraViewOn = false;
        }
        if(camera!=null)
        {
            try{
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                isCameraViewOn = true;
            }catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        camera = Camera.open();
        camera.setDisplayOrientation(90);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        camera.stopPreview();
        camera.release();
        camera = null;
        isCameraViewOn = false;
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent(this,MapActivity.class);
        startActivity(intent);
    }
}
