/*
 * Copyright (C) Skillworks AG 2008. All Rights Reserved Confidential
 */
package de.jacavi.appl.track;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;



public class QuadBezierSlotPart extends SlotPart {
    public Point controlPoint;

    public QuadBezierSlotPart(int length, Point entryPoint, Point controlPoint, Point exitPoint) {
        super(length, entryPoint, exitPoint);
        this.controlPoint = controlPoint;
    }

    @Override
    public Shape getShape() {
        return new QuadCurve2D.Double(entryPoint.x, entryPoint.y, controlPoint.x, controlPoint.y, exitPoint.x,
                exitPoint.y);
    }

    private Point2D computeQuadBezierPoint(double[][] referencePoints, double t) {
        final int[] binomialCoefficients = new int[] { 1, 2, 1 }; // stored statically for efficiency and simplicity
        double c[] = new double[2];
        int n = referencePoints.length - 1;

        for(int dim = 0; dim < 2; dim++)
            for(int i = 0; i <= n; i++)
                c[dim] += binomialCoefficients[i] * Math.pow(t, i) * Math.pow(1 - t, n - i) * referencePoints[i][dim];
        return new Point2D.Double(c[0], c[1]);
    }

    @Override
    public DirectedPoint getStepPoint(int position) {
        double[][] referencePoints = new double[3][];
        referencePoints[0] = new double[] { entryPoint.x, entryPoint.y };
        referencePoints[1] = new double[] { controlPoint.x, controlPoint.y };
        referencePoints[2] = new double[] { exitPoint.x, exitPoint.y };

        Point2D point = computeQuadBezierPoint(referencePoints, Double.valueOf(position) / Double.valueOf(length));
        Angle angle = new Angle(0);
        return new DirectedPoint(point, angle);
    }
}