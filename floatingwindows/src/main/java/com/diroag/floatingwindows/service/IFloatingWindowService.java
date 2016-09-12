package com.diroag.floatingwindows.service;

/**
 * Abstracción del servicio de ventana flotante
 */
public interface IFloatingWindowService {

    /**
     * Muestra la vista de ventana flotante adecuada
     * @param view vista
     */
    void show(AbstractFloatingWindowView view);

    /**
     * Cierra la ventana
     */
    void dismiss();
}
