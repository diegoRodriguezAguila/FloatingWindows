package com.diroag.floatingwindows.view;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.LinearLayout;

/**
 * Created by drodriguez on 09/09/2016.
 * flotable layout
 */
public class FloatableLayout extends LinearLayout {

    private OnBackListener mListener;

    public FloatableLayout(Context context) {
        super(context);
    }

    /**
     * Creates a new Floatable Layout which its layout params set
     * to wrap content
     * @param context context
     * @return {@link FloatableLayout}
     */
    public static FloatableLayout wrapLayout(Context context){
        FloatableLayout layout = new FloatableLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return layout;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if(mListener!=null)
                mListener.onBackPressed();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void setOnBackListener(OnBackListener listener){
        mListener = listener;
    }

    public interface OnBackListener{
        void onBackPressed();
    }
}