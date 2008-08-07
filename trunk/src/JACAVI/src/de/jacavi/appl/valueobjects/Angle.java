/*
 * Copyright (C) Skillworks AG 2008. All Rights Reserved Confidential
 */
package de.jacavi.appl.valueobjects;

public class Angle {
    public int angle;

    public Angle(int angle) {
        this.angle = angle % 360;
    }

    public void turn(Angle angle) {
        turn(angle.angle);
    }

    public void turn(int angle) {
        this.angle = (this.angle + angle) % 360;
    }

    public double getRadians() {
        return Math.toRadians(angle);
    }
}