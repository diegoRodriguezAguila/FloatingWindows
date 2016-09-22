package com.diroag.floatingwindows.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;

import com.diroag.floatingwindows.sample.floating.SampleView;
import com.diroag.floatingwindows.service.FloatingWindowController;

public class MainActivity extends AppCompatActivity {

    private SampleView mSampleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSampleView = new SampleView(this, "R7721");
        FloatingWindowController fwc = FloatingWindowController.create(this);
        fwc.showAtLocation(mSampleView, Gravity.TOP | Gravity.START, 500, 500);
        fwc.show(new SampleView(this, "OTHER"));
    }
}
