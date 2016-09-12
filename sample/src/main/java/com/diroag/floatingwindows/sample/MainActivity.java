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
import com.diroag.floatingwindows.service.IFloatingWindowService;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private IFloatingWindowService mService;
    private SampleView mSampleView;
    private boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSampleView = new SampleView(this, "R7721");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, FloatingWindowService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(this);
            mBound = false;
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
        mBound = true;
        mService.show(mSampleView);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }
}
