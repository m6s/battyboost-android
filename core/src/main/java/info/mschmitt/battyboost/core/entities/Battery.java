package info.mschmitt.battyboost.core.entities;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class Battery implements Serializable {
    public String qr;
    public long manufacturingTime;
    public int chargeCycleCount;
    public long borrowTime;
}
