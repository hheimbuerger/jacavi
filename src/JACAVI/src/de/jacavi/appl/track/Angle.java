/*
 * Copyright (C) Skillworks AG 2008. All Rights Reserved Confidential
 */
package de.jacavi.appl.track;

public class Angle {
    public int angle;

    public Angle(int angle) {
        normalizeAndSet(angle);
    }

    private void normalizeAndSet(int angle) {
        this.angle = (angle % 360) < 0 ? ((angle % 360) + 360) : (angle % 360);
    }

    public void turn(Angle turningAngle) {
        turn(turningAngle.angle);
    }

    public void turn(int turningAngle) {
        normalizeAndSet(this.angle + turningAngle);
    }

    public double getRadians() {
        return Math.toRadians(angle);
    }

    public void set(int newAngle) {
        normalizeAndSet(newAngle);
    }

    @Override
    public String toString() {
        return getClass().getName() + '[' + angle + ']';
    }
}