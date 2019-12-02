package com.CaLLIek.androidhelp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.io.IOException;
import java.util.List;


public class FlashLight extends AppCompatActivity implements  SoundPool.OnLoadCompleteListener {

    private int sound;
    private SoundPool soundPool;
    private Camera camera;
    Camera.Parameters parameters;
    private Switch mySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_light);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP)
        {
            createSoundPoolWithBuilder();
        }
        else
        {
            createSoundPoolWithConstructor();
        }


        soundPool.setOnLoadCompleteListener((SoundPool.OnLoadCompleteListener) this);
        sound = soundPool.load(this,R.raw.click,1);

        mySwitch = findViewById(R.id.switch_flashlight);
        mySwitch.setChecked(false);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    setFlashLightOn();
                }
                else
                {
                    setFlashLightOff();
                }
            }
        });

        boolean isCameraFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if(!isCameraFlash)
        {
            showCameraAlert();
        }
        else
        {
            camera = Camera.open();
        }

    }

    private void showCameraAlert()
    {
        new AlertDialog.Builder(this)
                .setTitle("Error! There are no flashLight")
                .setMessage("FlashLight not available")
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(R.drawable.ic_launcher_background)
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void createSoundPoolWithBuilder()
    {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(1).build();
    }

    protected void createSoundPoolWithConstructor()
    {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
    }

    private void setFlashLightOn()
    {
        soundPool.play(sound,1,1,0,0,1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(camera != null)
                {
                    parameters = camera.getParameters();
                    if(parameters!=null)
                    {
                        List supportedFlashMode = parameters.getSupportedFlashModes();

                        if(supportedFlashMode.contains(Camera.Parameters.FLASH_MODE_TORCH))
                        {
                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        }else if(supportedFlashMode.contains(Camera.Parameters.FLASH_MODE_ON))
                             {
                                 parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                             }
                             else
                                 camera = null;
                        if(camera !=null)
                        {
                            camera.setParameters(parameters);
                            camera.startPreview();
                            try{
                                camera.setPreviewTexture(new SurfaceTexture(0));
                            }catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private void setFlashLightOff()
    {
        soundPool.play(sound,1,1,0,0,1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(camera !=null)
                {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameters);
                    camera.stopPreview();
                }
            }
        }).start();
    }

    private void releaseCamera()
    {
        if(camera !=null)
        {
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        releaseCamera();

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(camera == null)
        {
            camera = Camera.open();
        }else
        {
            setFlashLightOn();
        }
        mySwitch.setChecked(true);

    }

    @Override
    public void onLoadComplete(SoundPool soundPool,int i, int il)
    {

    }
}
