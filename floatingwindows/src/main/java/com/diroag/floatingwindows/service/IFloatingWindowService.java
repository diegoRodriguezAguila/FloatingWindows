package com.diroag.floatingwindows.service;

/**
 * Floating window service abstraction
 */
interface IFloatingWindowService {

    /**
     * Shows the view as a floating window
     * @param view view
     */
    void show(FloatingWindowView view);

    /**
     * Shows all the hidden floating window views
     */
    void showAll();

    /**
     * Closes the floating window
     * @param view view
     */
    void dismiss(FloatingWindowView view);

    /**
     * Closes all the floating window views
     */
    void dismissAll();

    /**
     * Hides all the floating window views
     */
    void hideAll();

    /**
     * Prevents the floating window's position to be changed
     * @param view view
     */
    void lockPosition(FloatingWindowView view);

    /**
     * Locks the position of all floating windows. If there are
     * windows already locked they doesn't change its state
     */
    void lockAll();

    /**
     * Permits the floating window's position to be changed
     * @param view view
     */
    void unlockPosition(FloatingWindowView view);

    /**
     * Unlocks the position of all floating windows. If there are
     * windows already locked they doesn't change its state
     */
    void unlockAll();
}
