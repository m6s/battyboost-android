package info.mschmitt.battyboost.core.entities;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class Pos implements Serializable {
    public String imageUrl;
    public String name;
    public String info;
    public String url;
    public double latitude;
    public double longitude;
    public int availableBatteryCount;
}
