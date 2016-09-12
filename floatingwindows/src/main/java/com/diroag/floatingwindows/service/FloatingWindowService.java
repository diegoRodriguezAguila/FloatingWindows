package com.diroag.floatingwindows.service;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.diroag.floatingwindows.utils.ActivityUtils;
import com.diroag.floatingwindows.view.BackListenerLayout;


public class FloatingWindowService extends Service implements IFloatingWindowService {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private transient boolean mIsWindowShown;
    private WindowManager windowManager;

    private View mRootView;

    private LayoutParams mParams;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    /**
     * Pone el touch listener a los campos necesarios
     */
    private void setTouchListener() {
        int x = mParams != null ? mParams.x : 90;
        int y = mParams != null ? mParams.y : 100;
        mParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_TOAST,
                LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        mParams.x = x;
        mParams.y = y;
        mParams.gravity = Gravity.TOP | Gravity.START;
        mRootView.setOnTouchListener(new FloatingWindowTouchListener());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Muestra el campo en ventana flotante
     */
    @Override
    public void show(FloatingWindowView view) {
        if (view == null)
            throw new IllegalArgumentException("view cannot be null");
        if (mIsWindowShown) {
            return;
        }
        if (mRootView == null) {
            mRootView = view.createView();
            setBackListener();
            view.bindToService(this);
            setTouchListener();
            reMeasureRootView();
            mRootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    // its possible that the layout is not complete in which case
                    // we will get all zero values for the positions, so ignore the event
                    if (left == 0 && top == 0 && right == 0 && bottom == 0) {
                        return;
                    }
                    if (reMeasureRootView())
                        windowManager.updateViewLayout(mRootView, mParams);
                }
            });
        }
        mIsWindowShown = true;
        windowManager.addView(mRootView, mParams);
    }

    private void setBackListener() {
        if (!(mRootView instanceof BackListenerLayout))
            return;
        ((BackListenerLayout) mRootView).setOnBackListener(new BackListenerLayout.OnBackListener() {
            @Override
            public void onBackPressed() {
                Activity activity = ActivityUtils.resolveActivity(mRootView.getContext());
                if (activity != null)
                    activity.onBackPressed();
            }
        });
    }

    @SuppressWarnings("Range")
    private boolean reMeasureRootView() {
        mRootView.measure(
                View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT,
                        View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT,
                        View.MeasureSpec.AT_MOST));
        int newHeight = mRootView.getMeasuredHeight();
        int newWidth = mRootView.getMeasuredWidth();
        if (mParams.height != newHeight || mParams.width != newWidth) {
            mParams.height = newHeight;
            mParams.width = newWidth;
            return true;
        }
        return false;
    }

    @Override
    public void dismiss() {
        if (mRootView == null || !mIsWindowShown) {
            return;
        }
        windowManager.removeView(mRootView);
        mIsWindowShown = false;
    }

    @Override
    public void lockPosition() {
        mRootView.setOnTouchListener(null);
    }

    @Override
    public void unlockPosition() {
        setTouchListener();
    }

    /**
     * Devuelve true si la vista ya está mostrada
     *
     * @return true if is shown
     */
    public boolean isWindowShown() {
        return mIsWindowShown;
    }

    //region Inner Classes

    /**
     * Binder para la clase
     *
     * @author drodriguez
     */
    public class LocalBinder extends Binder {
        @NonNull
        public IFloatingWindowService getService() {
            return FloatingWindowService.this;
        }
    }

    /**
     * Clase de touch listener para la vista de la
     * ventana flotante
     */
    private class FloatingWindowTouchListener implements View.OnTouchListener {

        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;

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
                    windowManager.updateViewLayout(mRootView,
                            mParams);
                    return true;
            }
            return false;
        }
    }

    //endregion
}
