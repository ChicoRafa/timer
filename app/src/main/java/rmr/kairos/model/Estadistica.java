package rmr.kairos.model;

/**
 * Clase modelo con las catacterísticas de una estadística
 * @author Rafa M.
 * @version 1.0
 * @since 1.0
 */
public class Estadistica {
    private String dia;
    private int workTime;

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public int getWorkTime() {
        return workTime;
    }

    public void setWorkTime(int workTime) {
        this.workTime = workTime;
    }
}