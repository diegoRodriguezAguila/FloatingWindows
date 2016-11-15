package com.diroag.floatingwindows.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

/**
 * Created by Diego on 10/7/2016.
 * Activity utils
 */
public class ActivityUtils {

    /**
     * Gets the activity of a context.
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
