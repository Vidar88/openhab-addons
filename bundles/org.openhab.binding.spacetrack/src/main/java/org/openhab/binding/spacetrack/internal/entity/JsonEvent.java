package org.openhab.binding.spacetrack.internal.entity;

import org.orekit.time.AbsoluteDate;
import org.orekit.utils.PVCoordinates;


public class JsonEvent {

    private boolean isIncreasing;
    private String date;
    private double mu;
    private PVCoordinates position;


    public boolean isIncreasing() {
        return isIncreasing;
    }

    public void setIncreasing(boolean increasing) {
        isIncreasing = increasing;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getMu() {
        return mu;
    }

    public void setMu(double mu) {
        this.mu = mu;
    }

    public PVCoordinates getPosition() {
        return position;
    }

    public void setPosition(PVCoordinates position) {
        this.position = position;
    }
}
