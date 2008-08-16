package de.jacavi.appl.track;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;




public class Slot {

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
