package com.diroag.floatingwindows.service;

/**
 * Floating window service abstraction
 */
public interface IFloatingWindowService {

    /**
     * Shows the view as a floating window
     * @param view vista
     */
    void show(FloatingWindowView view);

    /**
     * Closes the floating window
     */
    void dismiss();

    /**
     * Prevents the floating window's position to be changed
     */
    void lockPosition();

    /**
     * Permits the floating window's position to be changed
     */
    void unlockPosition();
}
