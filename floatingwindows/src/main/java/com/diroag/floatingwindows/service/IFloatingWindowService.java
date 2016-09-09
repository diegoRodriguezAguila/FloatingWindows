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
     * Esconde la ventana
     * @param minimize Si esta en true esconde la ventana con otra animación
     */
    void hide(boolean minimize);

    /**
     * Esconde la ventana y detiene el servicio
     */
    void exit();
}
