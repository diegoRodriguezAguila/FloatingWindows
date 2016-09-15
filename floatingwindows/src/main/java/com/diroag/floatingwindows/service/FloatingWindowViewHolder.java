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
class FloatingWindowViewHolder {
    private FloatingWindowView mWindowView;
    private View mRootView;
    private WindowManager.LayoutParams mParams;
    private LayoutListener mListener;

    public FloatingWindowViewHolder(FloatingWindowView view) {
        this(view, Gravity.TOP | Gravity.START, 0, 0);
    }

    public FloatingWindowViewHolder(FloatingWindowView view, int gravity, int x, int y) {
        this.mWindowView = view;
        this.mRootView = view.createView();
        mParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        mParams.gravity = gravity;
        mParams.x = x;
        mParams.y = y;
        setTouchListener();
        setBackListener();
        reMeasureRootView();
        setWindowLayoutListener();
    }

    /**
     * Sets the layout listener to get updates about the window layout changes
     *
     * @param listener layout listener {@link LayoutListener}
     */
    void setLayoutListener(LayoutListener listener) {
        mListener = listener;
    }


    void setTouchListener() {
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

    private void setWindowLayoutListener() {
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

    FloatingWindowView getWindowView(){
        return mWindowView;
    }

    View getRootView(){
        return mRootView;
    }

    WindowManager.LayoutParams getLayoutParams(){
        return mParams;
    }

    void setPosition(int gravity, int x, int y){
        mParams.gravity = gravity;
        mParams.x = x;
        mParams.y = y;
        reMeasureRootView();
        if (mListener != null)
            mListener.notifyLayoutUpdate(mRootView, mParams);
    }

    /**
     * If its inner rootViews are equals they are equals
     * @param obj other object
     * @return true if both rootViews are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FloatingWindowViewHolder) ||
                this.mRootView == null) {
            return false;
        }
        FloatingWindowViewHolder other = (FloatingWindowViewHolder) obj;
        return mRootView.equals(other.mRootView);
    }

    //region Inner interfaces

    /**
     * Observable for window requests
     */
    interface LayoutListener {
        void notifyLayoutUpdate(View rootView, WindowManager.LayoutParams params);
    }

    //endregion
}
