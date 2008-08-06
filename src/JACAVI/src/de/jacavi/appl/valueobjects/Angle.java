/*
 * Copyright (C) Skillworks AG 2008. All Rights Reserved Confidential
 */
package de.jacavi.appl.valueobjects;

public class Angle {
    public int angle;

    public Angle(int angle) {
        this.angle = angle;
    }

    public void turn(Angle angle) {
        this.angle += angle.angle;
        if(this.angle < 0)
            this.angle += 360;
        if(this.angle >= 360)
            this.angle -= 360;
    }

    public double getRadians() {
        return Math.toRadians(angle);
    }
}