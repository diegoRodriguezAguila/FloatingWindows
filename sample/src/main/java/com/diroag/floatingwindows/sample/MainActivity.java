package com.diroag.floatingwindows.sample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.diroag.floatingwindows.R;
import com.diroag.floatingwindows.sample.floating.SampleView;
import com.diroag.floatingwindows.service.FloatingWindowService;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private FloatingWindowService mService;
    private SampleView mSampleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent serviceIntent = new Intent(getApplication(), FloatingWindowService.class);
        startService(serviceIntent);
        bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mService != null) {
            mService.show(mSampleView);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mService != null)
            mService.dismiss();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        FloatingWindowService.LocalBinder binder = (FloatingWindowService.LocalBinder) service;
        mService = binder.getService();
        mSampleView = new SampleView(this, "R7721");
        mService.show(mSampleView);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }
}
