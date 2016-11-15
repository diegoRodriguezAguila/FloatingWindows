package com.diroag.floatingwindows.service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.diroag.floatingwindows.utils.ViewUtils;
import com.diroag.floatingwindows.view.BackListenerLayout;

/**
 * Floating window view abstraction. Views desired to be shown as floating windows must inherit
 * this class and implement the method: {@link #onCreateView(LayoutInflater)}
 */
public abstract class FloatingWindowView {

    private int mId;
    private boolean mWindowShowed;
    private boolean mLocked;
    private Context mContext;
    private IFloatingWindowService mService;

    public FloatingWindowView(Context context) {
        mId = ViewUtils.generateViewId();
        mContext = context;
    }

    /**
     * Gets the assigned FloatingWindowView id
     *
     * @return id
     */
    public int getId() {
        return mId;
    }

    /**
     * The context used to inflate and handle this view
     *
     * @return context
     */
    public Context getContext() {
        return mContext;
    }

    IFloatingWindowService getService() {
        if (mService == null)
            throw new IllegalStateException("Service was not set nor binded to this view! cannot " +
                    "use getService()");
        return mService;
    }

    void bindToService(IFloatingWindowService service) {
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
        if (mService != null) {
            mService.lockPosition(this);
        }
    }

    /**
     * Permits the floating window's position to be changed
     */
    public void unlockPosition() {
        if (mService != null) {
            mService.unlockPosition(this);
        }
    }

    /**
     * Closes the floating window
     */
    public void dismiss() {
        if (mService != null) {
            mService.dismiss(this);
        }
    }

    /**
     * True if the floating window's position is locked
     *
     * @return true if locked
     */
    public boolean isLocked() {
        return mLocked;
    }

    /**
     * Sets the variable of locked to true or false
     *
     * @param locked locked or not
     */
    void setLocked(boolean locked) {
        this.mLocked = locked;
    }

    /**
     * True if the floating window is being displayed
     *
     * @return true if displayed
     */
    public boolean isWindowShowed() {
        return mWindowShowed;
    }

    /**
     * Sets the variable of showed to true or false
     *
     * @param showed showed or not
     */
    void setWindowShowed(boolean showed) {
        this.mWindowShowed = showed;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FloatingWindowView &&
                mId == ((FloatingWindowView) obj).mId;
    }

    @Override
    public int hashCode() {
        return mId;
    }
}
