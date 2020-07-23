package edu.utep.cs.cs4381.platformer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PlatformView extends SurfaceView implements Runnable {

    private boolean debugging = true;
    private volatile boolean running;
    private Thread gameThread = null;

    private SurfaceHolder ourHolder;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder holder;

    private Context context;
    private long startFrameTime;
    private long timeThisFrame;
    private long fps;

    private LevelManager lm;
    private ViewPort vp;
    private InputController ic;

    public PlatformView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.context = context;
        holder = getHolder();
        paint = new Paint();

        vp = new ViewPort(screenWidth, screenHeight);
        loadLevel("LevelCave", 15, 2);
    }

    @Override
    public void run() {
        while (running) {
            startFrameTime = System.currentTimeMillis();
            update();
            draw();
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    private void update() {
        for (GameObject go : lm.gameObjects) {
            if (go.isActive()) {
                boolean clipped = vp.clipObject(go.getWorldLocation().x,
                        go.getWorldLocation().y, go.getWidth(),  go.getHeight()); {
                    go.setVisible(!clipped);
                }
            }
        }
    }


    private void draw() {
        if (holder.getSurface().isValid()){
            canvas = holder.lockCanvas();
            paint.setColor(Color.argb(255, 0, 0, 255));
            canvas.drawColor(Color.argb(255, 0, 0, 255));
            Paint name = new Paint();
            name.setColor(Color.WHITE);
            name.setTextSize(50);
            canvas.drawText("Gilbert", vp.getScreenWidth()/2, vp.getScreenHeight()/2,name);

            Rect toScreen2d = new Rect();
            for (int layer = -1; layer <= 1; layer++) {
                for (GameObject go : lm.gameObjects) {
                    if (go.isVisible() && go.getWorldLocation().z == layer) {
                        toScreen2d.set(vp.worldToScreen(go.getWorldLocation().x,  go.getWorldLocation().y, go.getWidth(), go.getHeight()));
                        canvas.drawBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())], toScreen2d.left, toScreen2d.top, paint);
                    }
                }
            }
            holder.unlockCanvasAndPost(canvas);
        }
    }



    public void pause() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("error", "failed to pause thread");
        }
    }

    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void loadLevel(String level, float px, float py) {
        ic = new InputController(vp.getScreenWidth(), vp.getScreenHeight());
        lm = new LevelManager(context, vp.getPixelsPerMetreX(),
                vp.getScreenWidth(), ic, level, px, py);
        vp.setWorldCentre(lm.gameObjects.get(lm.playerIndex).getWorldLocation().x, lm.gameObjects.get(lm.playerIndex).getWorldLocation().y);
    }


}


