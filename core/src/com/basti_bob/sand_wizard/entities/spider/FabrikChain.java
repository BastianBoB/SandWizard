package com.basti_bob.sand_wizard.entities.spider;

import javax.swing.text.Segment;
import java.util.ArrayList;

public class FabrikChain {

    private final ArrayList<FabrikSegment> segments;
    private float baseX, baseY;
    private float targetX, targetY;

    public FabrikChain(float baseX, float baseY) {
        this.baseX = baseX;
        this.baseY = baseY;

        segments = new ArrayList<>();
    }

    public void addSegment(float length, float angle) {
        if (segments.size() == 0) {
            segments.add(new FabrikSegment(baseX, baseY, angle, length));
        } else {
            FabrikSegment lastSegment = segments.get(segments.size() - 1);
            segments.add(new FabrikSegment(lastSegment.endX, lastSegment.endY, angle, length));
        }
    }

    public ArrayList<FabrikSegment> getSegments() {
        return segments;
    }

    public void addSegment(float length) {
        addSegment(length, 0);
    }

    public void resetSegmentsWithAngle(float angle) {

        for (int i = 0; i < segments.size(); i++) {
            FabrikSegment segment = segments.get(i);

            float startX = i == 0 ? segment.startX : segments.get(i - 1).endX;
            float startY = i == 0 ? segment.startY : segments.get(i - 1).endY;

            segment.reset(startX, startY, angle);
        }
    }

    public void setBasePosition(float x, float y) {
        this.baseX = x;
        this.baseY = y;
    }

    public void setTargetPosition(float x, float y) {
        this.targetX = x;
        this.targetY = y;
    }

    void update() {
        forwardReaching();
        backwardReaching();
    }

    void forwardReaching() {
        segments.get(segments.size() - 1).setEnd(targetX, targetY);

        for (int i = segments.size() - 2; i >= 0; i--) {
            FabrikSegment next = segments.get(i + 1);
            segments.get(i).setEnd(next.startX, next.startY);
        }
    }


    void backwardReaching() {
        segments.get(0).setStart(baseX, baseY);

        for (int i = 1; i < segments.size(); i++) {
            FabrikSegment previous = segments.get(i - 1);
            segments.get(i).setStart(previous.endX, previous.endY);
        }
    }

    public float getBaseX() {
        return baseX;
    }

    public float getBaseY() {
        return baseY;
    }

    public float getTargetX() {
        return targetX;
    }

    public float getTargetY() {
        return targetY;
    }
}
