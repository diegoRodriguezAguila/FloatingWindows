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
import android.util.SparseArray;

import com.diroag.floatingwindows.utils.ActivityUtils;

import java.util.ArrayDeque;
import java.util.Queue;

import static android.app.Application.ActivityLifecycleCallbacks;

/**
 * Controller for floating windows. This class is meant to manage all the floating windows.
 * It provides the required methods to handle window showing and hiding. To get an instance of
 * this class you must call static method {@link #create(Context)}
 */
public class FloatingWindowController implements IFloatingWindowService {

    private static final String TAG = "FloatingWindowControl";

    private IFloatingWindowService mService;
    private ActivityLifecycleCallbacks mActivityCallbacks;
    private Context mContext;
    private boolean mBound;
    private boolean mIsDestroyed;
    private Queue<FloatingWindowView> mPendingViews;
    private SparseArray<WindowPosition> mPendingViewLocations;
    private Queue<FloatingWindowView> mPendingLocks;
    private Queue<Runnable> mPendingTasks;
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
        this.mPendingViewLocations = new SparseArray<>();
        this.mPendingLocks = new ArrayDeque<>();
        this.mPendingTasks = new ArrayDeque<>();
        onCreate();
    }

    @Override
    public void showAtLocation(FloatingWindowView floatingWindow, int gravity, int x, int y) {
        synchronized (mLock) {
            if (mIsDestroyed)
                throw new IllegalStateException("FloatingWindowController is already destroyed, " +
                        "cannot call showAtLocation() method.");
            floatingWindow.bindToService(this);
            if (!mBound) {
                mPendingViewLocations.put(floatingWindow.getId(), new WindowPosition(gravity, x, y));
                mPendingViews.add(floatingWindow);
                return;
            }
            mService.showAtLocation(floatingWindow, gravity, x, y);
        }
    }

    /**
     * Shows the floating window view
     *
     * @param floatingWindow floating window view {@link FloatingWindowView}
     */
    @Override
    public void show(FloatingWindowView floatingWindow) {
        synchronized (mLock) {
            if (mIsDestroyed)
                throw new IllegalStateException("FloatingWindowController is already destroyed, " +
                        "cannot call show() method.");
            floatingWindow.bindToService(this);
            if (!mBound) {
                mPendingViews.add(floatingWindow);
                return;
            }
            mService.show(floatingWindow);
        }
    }

    @Override
    public void showAll() {
        synchronized (mLock) {
            if (mIsDestroyed)
                throw new IllegalStateException("FloatingWindowController is already destroyed, " +
                        "cannot call showAll() method.");
            if (!mBound) {
                mPendingTasks.add(new Runnable() {
                    @Override
                    public void run() {
                        showAll();
                    }
                });
                return;
            }
            mService.showAll();
        }
    }

    /**
     * Closes the floating window view
     *
     * @param floatingWindow floating window view {@link FloatingWindowView}
     */
    @Override
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

    @Override
    public void dismissAll() {
        synchronized (mLock) {
            if (mIsDestroyed)
                throw new IllegalStateException("FloatingWindowController is already destroyed, " +
                        "cannot call dismissAll() method.");
            if (!mBound) {
                mPendingTasks.add(new Runnable() {
                    @Override
                    public void run() {
                        dismissAll();
                    }
                });
                return;
            }
            mService.dismissAll();
        }
    }

    @Override
    public void hideAll() {
        synchronized (mLock) {
            if (mIsDestroyed)
                throw new IllegalStateException("FloatingWindowController is already destroyed, " +
                        "cannot call hideAll() method.");
            if (!mBound) {
                mPendingTasks.add(new Runnable() {
                    @Override
                    public void run() {
                        hideAll();
                    }
                });
                return;
            }
            mService.hideAll();
        }
    }

    /**
     * Locks the floating window position so it can't be moved
     *
     * @param floatingWindow floating window view {@link FloatingWindowView}
     */
    @Override
    public void lockPosition(FloatingWindowView floatingWindow) {
        synchronized (mLock) {
            if (mIsDestroyed)
                throw new IllegalStateException("FloatingWindowController is already destroyed, " +
                        "cannot call lockPosition() method.");
            if (!mBound) {
                mPendingLocks.add(floatingWindow);
                return;
            }
            mService.lockPosition(floatingWindow);
        }
    }

    @Override
    public void lockAll() {
        synchronized (mLock) {
            if (mIsDestroyed)
                throw new IllegalStateException("FloatingWindowController is already destroyed, " +
                        "cannot call lockAll() method.");
            if (!mBound) {
                mPendingTasks.add(new Runnable() {
                    @Override
                    public void run() {
                        lockAll();
                    }
                });
                return;
            }
            mService.lockAll();
        }
    }

    /**
     * Unlocks the floating window position so it can be moved
     *
     * @param floatingWindow floating window view {@link FloatingWindowView}
     */
    @Override
    public void unlockPosition(FloatingWindowView floatingWindow) {
        synchronized (mLock) {
            if (mIsDestroyed)
                throw new IllegalStateException("FloatingWindowController is already destroyed, " +
                        "cannot call unlockPosition() method.");
            if (!mBound) {
                mPendingLocks.remove(floatingWindow);
                return;
            }
            mService.unlockPosition(floatingWindow);
        }
    }

    @Override
    public void unlockAll() {
        synchronized (mLock) {
            if (mIsDestroyed)
                throw new IllegalStateException("FloatingWindowController is already destroyed, " +
                        "cannot call unlockAll() method.");
            if (!mBound) {
                mPendingTasks.add(new Runnable() {
                    @Override
                    public void run() {
                        unlockAll();
                    }
                });
                return;
            }
            mService.unlockAll();
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
     * Destroys the references and stop the floating windows services. This must be called
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
        showPendingViews();
        lockPendingViews();
        execPendingTasks();
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
            mPendingViewLocations.clear();
            mPendingViewLocations = null;
            mPendingLocks.clear();
            mPendingLocks = null;
            mPendingTasks.clear();
            mPendingTasks = null;
            mIsDestroyed = true;
        }
    }

    private void showPendingViews() {
        FloatingWindowView floatingWindow = mPendingViews.poll();
        while (floatingWindow != null) {
            WindowPosition pos = mPendingViewLocations.get(floatingWindow.getId());
            if (pos != null)
                mService.showAtLocation(floatingWindow, pos.gravity, pos.x, pos.y);
            else mService.show(floatingWindow);
            floatingWindow = mPendingViews.poll();
        }
        mPendingViewLocations.clear();
    }

    private void lockPendingViews() {
        FloatingWindowView floatingWindow = mPendingLocks.poll();
        while (floatingWindow != null) {
            mService.lockPosition(floatingWindow);
            floatingWindow = mPendingLocks.poll();
        }
    }

    private void execPendingTasks() {
        Runnable task = mPendingTasks.poll();
        while (task != null) {
            task.run();
            task = mPendingTasks.poll();
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

    private static class WindowPosition {
        public int gravity;
        public int x;
        public int y;

        public WindowPosition() {
        }

        public WindowPosition(int gravity, int x, int y) {
            this.gravity = gravity;
            this.x = x;
            this.y = y;
        }
    }
}
