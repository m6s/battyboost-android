package info.mschmitt.battyboost.core.entities;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class Pos implements Serializable {
    public String name;
    public String info;
    public String imageUrl;
    public String url;
    public int availableBatteryCount;
    public double latitude;
    public double longitude;
}
