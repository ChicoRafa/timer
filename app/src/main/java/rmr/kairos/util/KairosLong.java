package rmr.kairos.util;

/**
 * Envoltorio mutable para el tipo long
 */
public class KairosLong{
    private long value;
    public KairosLong(long value) {
        this.value = value;
    }
    public long getValue() {
        return value;
    }
    public void setValue(long value) {
        this.value = value;
    }
}
