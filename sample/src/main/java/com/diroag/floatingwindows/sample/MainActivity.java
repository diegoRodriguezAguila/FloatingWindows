package com.diroag.floatingwindows.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;

import com.diroag.floatingwindows.sample.floating.SampleView;
import com.diroag.floatingwindows.service.FloatingWindowController;

import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

public class MainActivity extends AppCompatActivity {

    private SampleView mSampleView;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(getMainLooper());
        setContentView(R.layout.activity_main);
        mSampleView = new SampleView(this, "R7721");
        FloatingWindowController fwc = FloatingWindowController.create(this);
        handler.postDelayed(()-> fwc.show(mSampleView), 3000);
        fwc.showAtLocation(mSampleView, Gravity.TOP | Gravity.START, 500, 500, FLAG_NOT_FOCUSABLE);
        handler.postDelayed(()-> fwc.show(mSampleView), 3000);
        mSampleView.dismissListener = () -> handler.postDelayed(()-> fwc.show(mSampleView), 3000);
    }
}
