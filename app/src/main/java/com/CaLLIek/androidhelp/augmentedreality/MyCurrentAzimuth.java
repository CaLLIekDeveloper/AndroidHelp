package com.CaLLIek.androidhelp.augmentedreality;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Android Studio.
 * User: CaLLIek
 * Date: 04.12.2019
 * Time: 13:43
 */

public class MyCurrentAzimuth implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private int azimuthFrom = 0;
    private int azimuthTo = 0;
    private OnAzimuthChangedListener onAzimuthChangedListener;
    Context mContext;

    public MyCurrentAzimuth(OnAzimuthChangedListener azimuthChangedListener, Context context)
    {
        onAzimuthChangedListener = azimuthChangedListener;
        mContext = context;
    }

    public void start()
    {
        sensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_UI);
    }

    public void stop()
    {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        azimuthFrom = azimuthTo;

        float[] orientation = new float[3];
        float[] rMat = new float[9];
        SensorManager.getRotationMatrixFromVector(rMat,event.values);
        azimuthTo = (int) (Math.toDegrees(SensorManager.getOrientation(rMat,orientation)[0])+360) %360;

        onAzimuthChangedListener.onAzimuthChanged(azimuthFrom,azimuthTo);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
