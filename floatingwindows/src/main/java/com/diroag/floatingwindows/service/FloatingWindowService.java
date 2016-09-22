package com.diroag.floatingwindows.service;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;


public class FloatingWindowService extends Service implements IFloatingWindowService {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private WindowManager windowManager;
    private List<FloatingWindowViewHolder> mFloatingWindows;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mFloatingWindows = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        this.dismissAll();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Finds the viewholder
     *
     * @param view view
     * @return viewholder
     */
    private FloatingWindowViewHolder findViewHolder(FloatingWindowView view) {
        for (FloatingWindowViewHolder holder : mFloatingWindows) {
            if (holder.getWindowView().equals(view)) {
                return holder;
            }
        }
        return null;
    }

    /**
     * Shows a window
     *
     * @param viewHolder view holder
     */
    private void show(FloatingWindowViewHolder viewHolder) {
        FloatingWindowView view = viewHolder.getWindowView();
        if (view.isWindowShowed()) {
            return;
        }
        viewHolder.setLayoutListener(new FloatingWindowViewHolder.LayoutListener() {
            @Override
            public void notifyLayoutUpdate(View rootView, WindowManager.LayoutParams params) {
                windowManager.updateViewLayout(rootView, params);
            }
        });
        windowManager.addView(viewHolder.getRootView(), viewHolder.getLayoutParams());
        view.setWindowShowed(true);
    }

    /**
     * Removes a window and removes from the window list
     *
     * @param viewHolder view holder
     */
    private boolean dismiss(FloatingWindowViewHolder viewHolder) {
        return hide(viewHolder) && mFloatingWindows.remove(viewHolder);
    }

    /**
     * Hides a window. but it doesn't get removed from the window list
     *
     * @param viewHolder view holder
     * @return true if it could be hide
     */
    private boolean hide(FloatingWindowViewHolder viewHolder) {
        FloatingWindowView view = viewHolder.getWindowView();
        if (viewHolder.getRootView() == null || !view.isWindowShowed()) {
            return false;
        }
        windowManager.removeView(viewHolder.getRootView());
        view.setWindowShowed(false);
        return true;
    }

    /**
     * Locks the position of the window
     *
     * @param viewHolder view holder
     */
    private void lock(FloatingWindowViewHolder viewHolder) {
        FloatingWindowView view = viewHolder.getWindowView();
        if (view.isLocked())
            return;
        viewHolder.getRootView().setOnTouchListener(null);
        view.setLocked(true);
    }

    /**
     * Unlocks the position of the window
     *
     * @param viewHolder view holder
     */
    private void unlock(FloatingWindowViewHolder viewHolder) {
        FloatingWindowView view = viewHolder.getWindowView();
        if (!view.isLocked())
            return;
        viewHolder.setTouchListener();
        view.setLocked(false);
    }

    //region interface methods

    @Override
    public void showAtLocation(FloatingWindowView view, int gravity, int x, int y) {
        if (view == null)
            throw new IllegalArgumentException("view cannot be null");
        if(x<0 || y<0)
            throw new IllegalArgumentException("invalid x, y position");
        FloatingWindowViewHolder viewHolder = findViewHolder(view);
        if (viewHolder == null) {
            viewHolder = new FloatingWindowViewHolder(view, gravity, x, y);
            mFloatingWindows.add(viewHolder);
        } else viewHolder.setPosition(gravity, x, y);
        show(viewHolder);
    }

    @Override
    public void show(FloatingWindowView view) {
        if (view == null)
            throw new IllegalArgumentException("view cannot be null");
        FloatingWindowViewHolder viewHolder = findViewHolder(view);
        if (viewHolder == null) {
            viewHolder = new FloatingWindowViewHolder(view);
            mFloatingWindows.add(viewHolder);
        }
        show(viewHolder);
    }

    @Override
    public void showAll() {
        for (FloatingWindowViewHolder holder : mFloatingWindows) {
            show(holder);
        }
    }

    @Override
    public void dismiss(FloatingWindowView view) {
        FloatingWindowViewHolder viewHolder = findViewHolder(view);
        if (viewHolder == null)
            return;
        dismiss(viewHolder);
    }

    @Override
    public void dismissAll() {
        for (int i = 0; i < mFloatingWindows.size(); i++) {
            if (dismiss(mFloatingWindows.get(i)))
                i--;
        }
    }

    @Override
    public void hideAll() {
        for (FloatingWindowViewHolder holder : mFloatingWindows) {
            hide(holder);
        }
    }

    @Override
    public void lockPosition(FloatingWindowView view) {
        FloatingWindowViewHolder viewHolder = findViewHolder(view);
        if (viewHolder == null)
            return;
        lock(viewHolder);
    }

    @Override
    public void lockAll() {
        for (FloatingWindowViewHolder holder : mFloatingWindows) {
            lock(holder);
        }
    }

    @Override
    public void unlockPosition(FloatingWindowView view) {
        FloatingWindowViewHolder viewHolder = findViewHolder(view);
        if (viewHolder == null)
            return;
        unlock(viewHolder);
    }

    @Override
    public void unlockAll() {
        for (FloatingWindowViewHolder holder : mFloatingWindows) {
            unlock(holder);
        }
    }

    //endregion

    //region Inner Classes

    /**
     * Binder for service
     */
    public class LocalBinder extends Binder {
        @NonNull
        public IFloatingWindowService getService() {
            return FloatingWindowService.this;
        }
    }

    //endregion
}
