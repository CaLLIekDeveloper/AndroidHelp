package com.CaLLIek.androidhelp.augmentedreality;

/**
 * Created by Android Studio.
 * User: CaLLIek
 * Date: 04.12.2019
 * Time: 14:26
 */

public class Pikachu {

    private String name;
    private double latitude;

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private double longitude;

    public Pikachu(String newName, double newLatitude, double newLongitude)
    {
        this.name = newName;
        this.latitude = newLatitude;
        this.longitude = newLongitude;
    }


}
