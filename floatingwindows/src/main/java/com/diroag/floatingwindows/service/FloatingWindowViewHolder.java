package com.diroag.floatingwindows.service;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.diroag.floatingwindows.utils.ActivityUtils;
import com.diroag.floatingwindows.view.BackListenerLayout;

/**
 * Created by drodriguez on 12/09/2016.
 * Floating windows values
 */
public class FloatingWindowViewHolder {
    private FloatingWindowView windowView;
    private boolean mIsWindowShowed;
    private View mRootView;
    private WindowManager.LayoutParams mParams;
    private LayoutListener mListener;

    public FloatingWindowViewHolder() {
        this(0, 0);
    }

    public FloatingWindowViewHolder(int x, int y) {
        mParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.TOP | Gravity.START;
        mParams.x = x;
        mParams.y = y;
        setTouchListener();
        setBackListener();
        reMeasureRootView();
        setWindowLayoutListener();
    }

    /**
     *
     * @return
     */
    boolean dismiss(){
        if (mRootView == null || !mIsWindowShowed) {
            return false;
        }
        mIsWindowShowed = false;
        return true;
    }

    /**
     * Sets the layout listener to get updates about the window layout changes
     * @param listener layout listener {@link LayoutListener}
     */
    void setLayoutListener(LayoutListener listener){
        mListener = listener;
    }

    /**
     * Sets the window show status
     * @param isShowed sets is showed
     */
    void setIsWindowShowed(boolean isShowed){
        mIsWindowShowed = isShowed;
    }

    /**
     * Returns the visibility status of the window
     * @return true if its being showed
     */
    public boolean isWindowShowed(){
        return mIsWindowShowed;
    }

    private void setTouchListener() {
        mRootView.setOnTouchListener(new FloatingWindowTouchListener(mParams) {
            @Override
            public void onParamsChanged() {
                if (mListener != null)
                    mListener.notifyLayoutUpdate(mRootView, mParams);
            }
        });
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

    private void setWindowLayoutListener(){
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
                    if (mListener != null)
                        mListener.notifyLayoutUpdate(mRootView, mParams);
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

    /**
     * Observable for window requests
     */
    interface LayoutListener {
        void notifyLayoutUpdate(View rootView, WindowManager.LayoutParams params);
    }
}
