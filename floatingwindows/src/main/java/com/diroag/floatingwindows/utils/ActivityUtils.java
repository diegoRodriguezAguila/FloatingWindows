package com.diroag.floatingwindows.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

/**
 * Created by Diego on 10/7/2016.
 * activity utils
 */
public class ActivityUtils {

    /**
     * Resolves the context's activity
     * @param context context
     * @return resolved activity, null if the context wasn't an activity
     */
    public static Activity resolveActivity(Context context) {
        if (context instanceof Activity)
            return (Activity) context;
        if (context instanceof ContextWrapper)
            return resolveActivity(((ContextWrapper) context).getBaseContext());
        return null;
    }
}
