/*
 * Copyright (C) Skillworks AG 2008. All Rights Reserved Confidential
 */
package de.jacavi.appl.track;

public class Angle {
    public int angle;

    public Angle(int angle) {
        this.angle = angle % 360;
    }

    public void turn(Angle turningAngle) {
        turn(turningAngle.angle);
    }

    public void turn(int turningAngle) {
        this.angle = (this.angle + turningAngle) % 360;
    }

    public double getRadians() {
        return Math.toRadians(angle);
    }

    public void set(int newAngle) {
        angle = newAngle;
    }
}