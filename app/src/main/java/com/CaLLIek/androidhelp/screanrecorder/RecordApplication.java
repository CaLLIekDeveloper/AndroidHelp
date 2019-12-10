package com.CaLLIek.androidhelp.screanrecorder;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Android Studio.
 * User: CaLLIek
 * Date: 11.12.2019
 * Time: 00:25
 */

public class RecordApplication extends Application {

    private static RecordApplication application;

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        application = this;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        startService(new Intent(this,RecordService.class));
    }

    public static RecordApplication getInstance()
    {
        return application;
    }
}
