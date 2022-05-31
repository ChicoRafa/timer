package rmr.kairos.interfaces;

/**
 * Interfaz para facilitar la comunicación de los hilos secundarios con el hilo principal
 * de la aplicación
 */
public interface LayoutUpdatable {
    void updateSessionText(int sessionCount);
    void updateTimerText(String text);
    void updateStateText(String text);
    void clearSessionText(String text);
}
