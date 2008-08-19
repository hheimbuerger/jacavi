package de.jacavi.appl.track;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import de.jacavi.appl.track.SlotPart.DirectedPoint;



public class Slot {

    private List<SlotPart> slotParts = new ArrayList<SlotPart>();

    public void addLine(int length, int x1, int y1, int x2, Integer y2) {
        Point entryPoint = new Point(x1, y1);
        Point exitPoint = new Point(x2, y2);
        slotParts.add(new LineSlotPart(length, entryPoint, exitPoint));
    }

    public void addQuadBezier(int length, int x1, int y1, int x2, int y2, int x3, int y3, int entryToExitAngle) {
        Point entryPoint = new Point(x1, y1);
        Point controlPoint = new Point(x2, y2);
        Point exitPoint = new Point(x3, y3);
        slotParts.add(new QuadBezierSlotPart(length, entryPoint, controlPoint, exitPoint, entryToExitAngle));
    }

    public List<SlotPart> getSlotParts() {
        return slotParts;
    }

    public int getLength() {
        int length = 0;
        for(SlotPart sp: slotParts)
            length += sp.length;
        return length;
    }

    public DirectedPoint getStepPoint(int position) {
        int currentPos = 0;
        Angle currentAngle = new Angle(0);
        for(SlotPart sp: slotParts) {
            if(position < currentPos + sp.length) {
                DirectedPoint stepPoint = sp.getStepPoint(position - currentPos);
                stepPoint.angle.turn(currentAngle);
                return stepPoint;
            }
            currentPos += sp.length;
            currentAngle.turn(sp.entryToExitAngle);
        }
        return null;
    }
}
