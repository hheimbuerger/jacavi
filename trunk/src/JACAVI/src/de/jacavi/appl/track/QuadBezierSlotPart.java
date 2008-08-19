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

    private double[][] referencePoints = new double[3][];

    private double distP0P1, distP1P2, distP0P2;

    public QuadBezierSlotPart(int length, Point entryPoint, Point controlPoint, Point exitPoint) {
        super(length, entryPoint, exitPoint);
        this.controlPoint = controlPoint;

        // extract to double array for easier computation
        referencePoints[0] = new double[] { entryPoint.x, entryPoint.y };
        referencePoints[1] = new double[] { controlPoint.x, controlPoint.y };
        referencePoints[2] = new double[] { exitPoint.x, exitPoint.y };

        // compute the lengths of the three sides formed by the triangle.
        distP1P2 = Math.sqrt(Math.pow(referencePoints[2][0] - referencePoints[1][0], 2)
                + Math.pow(referencePoints[2][1] - referencePoints[1][1], 2));
        distP0P1 = Math.sqrt(Math.pow(referencePoints[1][0] - referencePoints[0][0], 2)
                + Math.pow(referencePoints[1][1] - referencePoints[0][1], 2));
        distP0P2 = Math.sqrt(Math.pow(referencePoints[2][0] - referencePoints[0][0], 2)
                + Math.pow(referencePoints[2][1] - referencePoints[0][1], 2));
    }

    @Override
    public Shape getShape() {
        return new QuadCurve2D.Double(entryPoint.x, entryPoint.y, controlPoint.x, controlPoint.y, exitPoint.x,
                exitPoint.y);
    }

    private Point2D computeQuadBezierPoint(double t) {
        final int[] binomialCoefficients = new int[] { 1, 2, 1 }; // stored statically for efficiency and simplicity
        double c[] = new double[2];
        int n = referencePoints.length - 1;

        for(int dim = 0; dim < 2; dim++)
            for(int i = 0; i <= n; i++)
                c[dim] += binomialCoefficients[i] * Math.pow(t, i) * Math.pow(1 - t, n - i) * referencePoints[i][dim];
        return new Point2D.Double(c[0], c[1]);
    }

    private double computeLawOfCosinesRadiansAngle(double oppositeLeg, double leftLeg, double rightLeg) {
        return Math.acos((Math.pow(leftLeg, 2) + Math.pow(rightLeg, 2) - Math.pow(oppositeLeg, 2))
                / (2 * leftLeg * rightLeg));
    }

    private double computeLawOfCosinesThirdLeg(double leftLeg, double rightLeg, double alpha) {
        return Math.sqrt(Math.pow(leftLeg, 2) + Math.pow(rightLeg, 2) - 2 * leftLeg * rightLeg * Math.cos(alpha));
    }

    private Angle computerQuadBezierAngle(double t) {
        // The comments in this method assume you know
        // http://upload.wikimedia.org/wikipedia/commons/3/35/Bezier_quadratic_anim.gif

        // First, we need an angle inside the triangle. We can calculate one using the three known legs of the
        // complete triangle describing the Bézier curve.
        double angleAtP1 = computeLawOfCosinesRadiansAngle(distP0P2, distP1P2, distP0P1);

        // Next, we need the length of the tangent (green line). We'll use the upper triangle formed by the green line,
        // part of P0-P1 and part of P1-P2.
        double lengthOfTangent = computeLawOfCosinesThirdLeg(distP1P2 * t, distP0P1 * (1 - t), angleAtP1);

        // Now we can calculate the angle between the tangent (green line) and P0-P1 using the same triangle as in the
        // last calculation.
        double angleAtBezierPoint = computeLawOfCosinesRadiansAngle(distP1P2 * t, distP0P1 * (1 - t), lengthOfTangent);

        // Now convert the radians angle into degrees.
        double relativeAngle = Math.toDegrees(angleAtBezierPoint);

        // And return it as a new Angle instance.
        return new Angle(new Double(relativeAngle).intValue());
    }

    @Override
    public DirectedPoint getStepPoint(int position) {
        double t = Double.valueOf(position) / Double.valueOf(length);
        Point2D point = computeQuadBezierPoint(t);
        Angle angle = computerQuadBezierAngle(t);
        return new DirectedPoint(point, angle);
    }
}