package com.diroag.floatingwindows.service;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Touch listener for floating window
 */
abstract class FloatingWindowTouchListener implements View.OnTouchListener {

    private WindowManager.LayoutParams mParams;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;

    FloatingWindowTouchListener(WindowManager.LayoutParams params){
        mParams = params;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = mParams.x;
                initialY = mParams.y;
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                return true;
            case MotionEvent.ACTION_MOVE:
                mParams.x = initialX
                        + (int) (event.getRawX() - initialTouchX);
                mParams.y = initialY
                        + (int) (event.getRawY() - initialTouchY);
                onParamsChanged();
                return true;
        }
        return false;
    }

    public abstract void onParamsChanged();
}