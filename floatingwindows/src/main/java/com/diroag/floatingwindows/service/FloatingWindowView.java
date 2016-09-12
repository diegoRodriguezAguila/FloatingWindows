package com.diroag.floatingwindows.service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.diroag.floatingwindows.view.BackListenerLayout;

/**
 * Abstracción de cualquier vista para la ventana flotante
 */
public abstract class FloatingWindowView {

    private boolean mIsLocked;
    private Context mContext;
    private IFloatingWindowService mService;

    public FloatingWindowView(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public IFloatingWindowService getService() {
        if (mService == null)
            throw new IllegalStateException("Service was not set nor binded to this view! cannot " +
                    "use getService()");
        return mService;
    }

    public void bindToService(IFloatingWindowService service) {
        if (service == null)
            throw new IllegalArgumentException("Service cannot be null");
        mService = service;
    }

    /**
     * Called when the view, contained in the floating window, is requested to be created
     *
     * @param inflater inflater to inflate view
     * @return view
     */
    public abstract
    @NonNull
    View onCreateView(LayoutInflater inflater);

    /**
     * Creates the view, by wrapping it in a {@link BackListenerLayout}
     *
     * @return view
     */
    View createView() {
        return BackListenerLayout
                .wrapView(onCreateView(LayoutInflater.from(mContext)));
    }

    /**
     * Prevents the floating window's position to be changed
     */
    public void lockPosition() {
        mIsLocked = true;
        mService.lockPosition();
    }

    /**
     * Permits the floating window's position to be changed
     */
    public void unlockPosition() {
        mService.unlockPosition();
        mIsLocked = false;
    }

    /**
     * True if the floating window's position is locked
     *
     * @return true if locked
     */
    public boolean isLocked() {
        return mIsLocked;
    }

}
