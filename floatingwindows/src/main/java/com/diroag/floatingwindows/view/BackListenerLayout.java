package com.diroag.floatingwindows.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * LinearLayout with a listener of back button
 */
public class BackListenerLayout extends LinearLayout {

    private OnBackListener mListener;

    public BackListenerLayout(Context context) {
        super(context);
    }

    /**
     * Creates a new Floatable Layout which its layout params set
     * to wrap content
     *
     * @param view view
     * @return {@link BackListenerLayout}
     */
    public static BackListenerLayout wrapView(@NonNull View view) {
        BackListenerLayout layout = new BackListenerLayout(view.getContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(view);
        return layout;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mListener != null)
                mListener.onBackPressed();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void setOnBackListener(OnBackListener listener) {
        mListener = listener;
    }

    public interface OnBackListener {
        void onBackPressed();
    }
}