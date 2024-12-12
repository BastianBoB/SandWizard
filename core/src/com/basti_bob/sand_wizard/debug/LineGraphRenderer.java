package com.basti_bob.sand_wizard.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.basti_bob.sand_wizard.player.Player;
import com.basti_bob.sand_wizard.util.MathUtil;
import com.basti_bob.sand_wizard.util.range.FloatRange;

import java.util.ArrayList;
import java.util.List;

public class LineGraphRenderer extends DebugRenderItem {

    private final int maxLength;
    private final List<Float> values = new ArrayList<>();
    private final ValueSupplier valueSupplier;
    private final FloatRange valueRange;
    private final ShapeRenderer shapeRenderer;

    private final boolean connectedLines;
    private final float connectThreshold;
    public LineGraphRenderer(DebugScreen debugScreen, float x, float y, float w, float h, boolean worldUpdate, ValueSupplier valueSupplier, FloatRange valueRange, int maxLength, boolean connectedLines) {
        super(debugScreen, x, y, w, h, worldUpdate);
        this.valueSupplier = valueSupplier;
        this.maxLength = maxLength;
        this.valueRange = valueRange;
        this.shapeRenderer = debugScreen.getGuiManager().getShapeRenderer();
        this.connectedLines = connectedLines;
        this.connectThreshold = (valueRange.max - valueRange.min) / 20f;
    }

    @Override
    public void update(Player player) {
        values.add(valueSupplier.get(player));

        if (values.size() > maxLength) {
            values.remove(0);
        }
    }

    @Override
    public void render() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.line(x, y, x, y - h);
        shapeRenderer.line(x, y - h, x + w, y - h);
        shapeRenderer.line(x, y, x + w, y);

        if(connectedLines) {
            drawConnectLineGraph();
        } else {
            drawWholeLineGraph();
        }

        shapeRenderer.end();
    }

    private void drawWholeLineGraph() {
        for (int i = 1; i < values.size() - 1; i++) {
            float previousVal = values.get(i - 1);
            float val = values.get(i);

            float x1 = MathUtil.map(i - 1, 0, values.size() - 1, x, x + w);
            float x2 = MathUtil.map(i, 0, values.size() - 1, x, x + w);

            float y1 = MathUtil.map(previousVal, valueRange.min, valueRange.max, y - h, y);
            float y2 = MathUtil.map(val, valueRange.min, valueRange.max, y - h, y);

            renderClippedLine(shapeRenderer, x1, y1, x2, y2, y);
        }
    }

    private void drawConnectLineGraph() {
        int lastConnectIndex = 0;
        float lastConnectValue = values.get(0);

        for (int i = 1; i < values.size() - 1; i++) {

            float previousVal = values.get(i - 1);
            float val = values.get(i);

            if ((i >= values.size() - 2) || Math.abs(val - lastConnectValue) > connectThreshold) {

                float x1 = MathUtil.map(lastConnectIndex, 0, values.size() - 1, x, x + w);
                float x2 = MathUtil.map(i - 1, 0, values.size() - 1, x, x + w);

                float y1 = MathUtil.map(lastConnectValue, valueRange.min, valueRange.max, y - h, y);
                float y2 = MathUtil.map(previousVal, valueRange.min, valueRange.max, y - h, y);

                renderClippedLine(shapeRenderer, x1, y1, x2, y2, y);

                lastConnectIndex = i - 1;
                lastConnectValue = previousVal;
            }
        }
    }

    public void renderClippedLine(ShapeRenderer shapeRenderer, float x1, float y1, float x2, float y2, float threshold) {
        if (y1 < threshold && y2 < threshold) {
            shapeRenderer.line(x1, y1, x2, y2);  // Both endpoints are below the threshold
            return;
        }

        if (y1 >= threshold && y2 >= threshold) { // Both endpoints are above the threshold
            return;
        }

        // One endpoint is above and the other is below the threshold, clip the line
        Vector2 intersection = calculateIntersection(new Vector2(x1, y1), new Vector2(x2, y2), threshold);
        if (y1 < threshold) {
            shapeRenderer.line(x1, y1, intersection.x, intersection.y);
        } else {
            shapeRenderer.line(intersection.x, intersection.y, x2, y2);
        }

    }

    private Vector2 calculateIntersection(Vector2 p1, Vector2 p2, float threshold) {
        float m = (p2.y - p1.y) / (p2.x - p1.x);
        float x = p1.x + (threshold - p1.y) / m;
        return new Vector2(x, threshold);
    }

    public interface ValueSupplier {
        float get(Player player);

    }
}
