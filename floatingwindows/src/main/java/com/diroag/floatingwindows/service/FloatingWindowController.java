package com.diroag.floatingwindows.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.diroag.floatingwindows.utils.ActivityUtils;

import java.util.ArrayDeque;
import java.util.Queue;

import static android.app.Application.ActivityLifecycleCallbacks;

/**
 * Created by drodriguez on 13/09/2016.
 * Controller for floating windows
 */
public class FloatingWindowController {

    private static final String TAG = "FloatingWindowControl";

    private IFloatingWindowService mService;
    private ActivityLifecycleCallbacks mActivityCallbacks;
    private Context mContext;
    private boolean mBound;
    private boolean mIsDestroyed;
    private Queue<FloatingWindowView> mPendingViews;
    private final Object mLock = new Object();

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized (mLock) {
                mBound = true;
                FloatingWindowService.LocalBinder binder = (FloatingWindowService.LocalBinder) service;
                mService = binder.getService();
                onBound();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    /**
     * Creates a new {@link FloatingWindowController} and initializes all required
     * services to manage floating windows.<br>
     * Don't forget to call {@link #destroy()} method (if your context is not an activity) to
     * prevent memory leaks. For Activities this is handled automatically
     *
     * @param context context
     * @return new instance of {@link FloatingWindowController}
     */
    public static FloatingWindowController create(@NonNull Context context) {
        return new FloatingWindowController(context);
    }

    /**
     * Private ctor. Please use {@link #create(Context)}
     * method in order to get an instance of this class
     *
     * @param context context
     */
    private FloatingWindowController(Context context) {
        this.mContext = context;
        this.mPendingViews = new ArrayDeque<>();
        onCreate();
    }

    /**
     * Shows the floating window view
     * @param floatingWindow floating window view {@link FloatingWindowView}
     */
    public void show(FloatingWindowView floatingWindow) {
        synchronized (mLock) {
            if (mIsDestroyed)
                throw new IllegalStateException("FloatingWindowController is already destroyed, " +
                        "cannot call show() method.");
            if (!mBound) {
                mPendingViews.add(floatingWindow);
                return;
            }
            mService.show(floatingWindow);
        }
    }

    /**
     * Closes the floating window view
     * @param floatingWindow floating window view {@link FloatingWindowView}
     */
    public void dismiss(FloatingWindowView floatingWindow) {
        synchronized (mLock) {
            if (mIsDestroyed)
                throw new IllegalStateException("FloatingWindowController is already destroyed, " +
                        "cannot call dismiss() method.");
            if (!mBound) {
                mPendingViews.remove(floatingWindow);
                return;
            }
            mService.dismiss(floatingWindow);
        }
    }

    /**
     * Checks if the service for floating windows was bound
     *
     * @return true if it was bound
     */
    public boolean isBound() {
        return mBound;
    }

    /**
     * Destroys the references and stop the floating windows services. this must be called
     * for non-Activity contexts like services.
     */
    public void destroy() {
        onDestroy();
    }

    /**
     * Initializes the {@link FloatingWindowService}
     */
    private void onCreate() {
        Intent intent = new Intent(mContext, FloatingWindowService.class);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        hookLifeCycle();
    }

    private void onBound() {
        FloatingWindowView floatingWindow = mPendingViews.poll();
        while (floatingWindow != null) {
            mService.show(floatingWindow);
            floatingWindow = mPendingViews.poll();
        }
    }

    private void onResume() {
        if (mService != null) {
            mService.showAll();
        }
    }

    private void onPause() {
        if (mService != null) {
            mService.hideAll();
        }
    }

    private void onDestroy() {
        synchronized (mLock) {
            if (mBound) {
                mBound = false;
                mContext.unbindService(mConnection);
            }
            if (mIsDestroyed) {
                return;
            }
            Log.d(TAG, "Destroying from onDestroy()");
            mService = null;
            mContext = null;
            mConnection = null;
            mActivityCallbacks = null;
            mPendingViews.clear();
            mPendingViews = null;
            mIsDestroyed = true;
        }
    }

    /**
     * Hooks to activity lifecylce
     */
    private void hookLifeCycle() {
        final Activity activity = ActivityUtils.resolveActivity(mContext);
        if (activity == null)
            return;
        mActivityCallbacks = new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity a, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity a) {
            }

            @Override
            public void onActivityResumed(Activity a) {
                if (activity != a)
                    return;
                onResume();
            }

            @Override
            public void onActivityPaused(Activity a) {
                if (activity != a)
                    return;
                onPause();
            }

            @Override
            public void onActivityStopped(Activity a) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity a, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity a) {
                if (activity != a)
                    return;
                activity.getApplication().unregisterActivityLifecycleCallbacks(mActivityCallbacks);
                onDestroy();
            }
        };
        activity.getApplication().registerActivityLifecycleCallbacks(mActivityCallbacks);
    }
}
