package edu.utep.cs.cs4381.platformer;

import android.graphics.Rect;

public class ViewPort {

    private int pixelsPerMetreX;
    private int pixelsPerMetreY;
    private int screenXResolution;
    private int screenYResolution;
    private float screenCentreX;
    private float screenCentreY;
    private int metresToShowX;
    private int metresToShowY;
    private int numClipped;
    private Rect convertedRect;
    private Vector2Point5D currentViewportWorldCentre;

    public ViewPort(int x, int y) {
        screenXResolution = x;
        screenYResolution = y;
        screenCentreX = screenXResolution / 2;
        screenCentreY = screenYResolution / 2;
        pixelsPerMetreX = screenXResolution / 32;
        pixelsPerMetreY = screenYResolution / 18;
        metresToShowX = 34;
        metresToShowY = 20;
        convertedRect = new Rect();
        currentViewportWorldCentre = new Vector2Point5D();
    }

    public Rect worldToScreen(float x, float y, float width, float height) {
        int left = (int) (screenCentreX - (currentViewportWorldCentre.x - x) * pixelsPerMetreX);
        int top = (int) (screenCentreY - (currentViewportWorldCentre.y - y) * pixelsPerMetreY);
        int right = (int) (left + width * pixelsPerMetreX);
        int bottom = (int) (top + height * pixelsPerMetreY);
        convertedRect.set(left, top, right, bottom);
        return convertedRect;

    }

    public boolean clipObject(float x, float y, float width, float height) {
        boolean isInside = (x - width < currentViewportWorldCentre.x + metresToShowX / 2)
                && (x + width > currentViewportWorldCentre.x - metresToShowX / 2)
                && (y - height < currentViewportWorldCentre.y + metresToShowY / 2)
                && (y + height > currentViewportWorldCentre.y - metresToShowY / 2);
        if (!isInside) { // for debugging
            numClipped++;
        }
        return !isInside;
    }


    public int getPixelsPerMetreX() {
        return pixelsPerMetreX;
    }

    public int getScreenWidth() {
        return screenXResolution;
    }

    public int getScreenHeight() {
        return screenYResolution;
    }

    public void setWorldCentre(float x, float y) {
        this.screenCentreX = x;
        this.screenCentreY = y;
    }
}//end of ViewPort class



