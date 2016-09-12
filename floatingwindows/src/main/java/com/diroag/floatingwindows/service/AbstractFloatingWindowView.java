package com.diroag.floatingwindows.service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Abstracción de cualquier vista para la ventana flotante
 */
public abstract class AbstractFloatingWindowView {

    private boolean mIsLocked;
    private Context mContext;
    private IFloatingWindowService mService;

    public AbstractFloatingWindowView(Context context){
        mContext = context;
    }

    public Context getContext(){
        return mContext;
    }

    public IFloatingWindowService getService(){
        if(mService == null)
            throw new IllegalStateException("Service was not set nor binded to this view! cannot " +
                    "use getService()");
        return mService;
    }

    public void bindToService(IFloatingWindowService service){
        if(service == null)
            throw new IllegalArgumentException("Service cannot be null");
        mService = service;
    }

    public abstract @NonNull View getRootView();

    /**
     * Prevents the floating window's position to be changed
     */
    public void lockPosition(){
        mIsLocked = true;
        mService.lockPosition();
    }

    /**
     * Permits the floating window's position to be changed
     */
    public void unlockPosition(){
        mService.unlockPosition();
        mIsLocked = false;
    }

    /**
     * True if the floating window's position is locked
     * @return true if locked
     */
    public boolean isLocked(){
        return mIsLocked;
    }

}
