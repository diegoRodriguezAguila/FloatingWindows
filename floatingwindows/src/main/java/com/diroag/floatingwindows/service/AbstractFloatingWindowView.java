package com.diroag.floatingwindows.service;

import android.content.Context;
import android.view.View;

/**
 * Abstracción de cualquier vista para la ventana flotante
 */
public abstract class AbstractFloatingWindowView {

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

    public abstract View getRootView();
}
