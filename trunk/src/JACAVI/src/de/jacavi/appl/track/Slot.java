package de.jacavi.appl.track;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.List;



public class Slot {

    public abstract class SlotPart {
        public int length;

        public Point entryPoint;

        public Point exitPoint;

        public SlotPart(int length, Point entryPoint, Point exitPoint) {
            this.length = length;
            this.entryPoint = entryPoint;
            this.exitPoint = exitPoint;
        }

        abstract public Shape getShape();

        abstract public Point2D getStep(int position);
    }

    public class LineSlotPart extends SlotPart {
        public LineSlotPart(int length, Point entryPoint, Point exitPoint) {
            super(length, entryPoint, exitPoint);
        }

        @Override
        public Shape getShape() {
            return new Line2D.Double(entryPoint.x, entryPoint.y, exitPoint.x, exitPoint.y);
        }

        @Override
        public Point2D getStep(int position) {
            return new Point2D.Double(entryPoint.x + (exitPoint.x - entryPoint.x) * position / Double.valueOf(length),
                    entryPoint.y + (exitPoint.y - entryPoint.y) * position / Double.valueOf(length));
        }
    }

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

        private int factorial(int x) {
            if(x <= 1)
                return 1;
            else {
                int result = 1;
                for(int i = 2; i <= x; i++)
                    result *= i;
                return result;
            }
        }

        private int binomialCoefficient(int n, int k) {
            return factorial(n) / (factorial(k) * factorial(n - k));
        }

        private Point2D computeBezierPoint(double[][] referencePoints, double t) {
            double c[] = new double[2];
            int n = referencePoints.length - 1;

            for(int dim = 0; dim < 2; dim++)
                for(int i = 0; i <= n; i++)
                    c[dim] += binomialCoefficient(n, i) * Math.pow(t, i) * Math.pow(1 - t, n - i)
                            * referencePoints[i][dim];
            return new Point2D.Double(c[0], c[1]);
        }

        @Override
        public Point2D getStep(int position) {
            double[][] referencePoints = new double[3][];
            referencePoints[0] = new double[] { entryPoint.x, entryPoint.y };
            referencePoints[1] = new double[] { controlPoint.x, controlPoint.y };
            referencePoints[2] = new double[] { exitPoint.x, exitPoint.y };

            return computeBezierPoint(referencePoints, Double.valueOf(position) / Double.valueOf(length));
        }
    }

    private List<SlotPart> slotParts = new ArrayList<SlotPart>();

    public void addLine(Integer length, Integer x1, Integer y1, Integer x2, Integer y2) {
        Point entryPoint = new Point(x1, y1);
        Point exitPoint = new Point(x2, y2);
        slotParts.add(new LineSlotPart(length, entryPoint, exitPoint));
    }

    public void addQuadBezier(Integer length, Integer x1, Integer y1, Integer x2, Integer y2, Integer x3, Integer y3) {
        Point entryPoint = new Point(x1, y1);
        Point controlPoint = new Point(x2, y2);
        Point exitPoint = new Point(x3, y3);
        slotParts.add(new QuadBezierSlotPart(length, entryPoint, controlPoint, exitPoint));
    }

    public List<SlotPart> getSlotParts() {
        return slotParts;
    }

}
