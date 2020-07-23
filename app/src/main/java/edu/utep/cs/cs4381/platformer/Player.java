package edu.utep.cs.cs4381.platformer;

import android.content.Context;

public class Player extends GameObject {

    private static final float HEIGHT = 2;
    private static final float WIDTH = 1;

    public Player(Context context, float worldStartX, float worldStartY, int pixelsPerMetre) {
        setHeight(HEIGHT); // 2 meters tall
        setWidth(WIDTH);   // 1 meter wide
        setType('p');
        setBitmapName("player");
        setWorldLocation(worldStartX, worldStartY, 0);
    }

    public void update(long fps, float gravity) {
    }
}