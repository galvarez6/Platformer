package edu.utep.cs.cs4381.platformer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

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

    private SoundManager sm;

    //private Rect rect;

    public PlatformView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.context = context;
        holder = getHolder();
        paint = new Paint();

        vp = new ViewPort(screenWidth, screenHeight);
        sm = SoundManager.instance(context);
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
                if (!vp.clipObject(go.getWorldLocation().x,
                        go.getWorldLocation().y,
                        go.getWidth(),
                        go.getHeight())) {
                    go.setVisible(true);

                    // check collisions with player
                    int hit = lm.player.checkCollisions(go.getHitbox());
                    if (hit > 0) {
                        switch (go.getType()) {
                            default:
                                if (hit == 1) { // left or right
                                    lm.player.setxVelocity(0);
                                    lm.player.setPressingRight(false);
                                }
                                if (hit == 2) { // feet
                                    lm.player.isFalling = false;
                                }
                                break;
                        }
                    }


                    if (lm.isPlaying()) {
                        go.update(fps, lm.gravity);
                    }

                } else {
                    go.setVisible(false);
                }
            }
        }//end of updating gameObjects
        if (lm.isPlaying()) {
            vp.setWorldCentre(
                    lm.player.getWorldLocation().x,
                    lm.player.getWorldLocation().y);
        }
    }


    private void draw() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            paint.setColor(Color.argb(255, 0, 0, 255));
            canvas.drawColor(Color.argb(255, 0, 0, 255));
            Paint name = new Paint();
            name.setColor(Color.WHITE);
            name.setTextSize(50);
            canvas.drawText("Gilbert", vp.getScreenWidth()/2, vp.getScreenHeight()/2,name);

            // draw paused text
            if (!lm.isPlaying()) {
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(120);
                canvas.drawText("Paused", vp.getScreenWidth() / 2,
                        vp.getScreenHeight() / 2, paint);
            }

            Rect toScreen2d = new Rect();
            for (int layer = -1; layer <= 1; layer++) {
                for (GameObject go : lm.gameObjects) {
                    if (go.isVisible() && go.getWorldLocation().z == layer) {
                        toScreen2d.set(vp.worldToScreen(go.getWorldLocation().x,  go.getWorldLocation().y, go.getWidth(), go.getHeight()));
                        canvas.drawBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())], toScreen2d.left, toScreen2d.top, paint);
                    }
                    if (go.isAnimated()) {
                        if (go.getFacing() == GameObject.RIGHT){ // rotate and draw?
                            Matrix flipper = new Matrix();
                            flipper.preScale(-1, 1);
                            Rect r = go.getRectToDraw(System.currentTimeMillis());
                            Bitmap b = Bitmap.createBitmap(lm.getBitmap(go.getType()),
                                    r.left, r.top, r.width(), r.height(), flipper, true);
                            canvas.drawBitmap(b, toScreen2d.left, toScreen2d.top, paint);
                        } else {
                            canvas.drawBitmap(lm.getBitmap(go.getType()), go.getRectToDraw(System.currentTimeMillis()), toScreen2d, paint);
                        }
                    } else { // no animation; just draw the whole bitmap
                        canvas.drawBitmap(lm.getBitmap(go.getType()),
                                toScreen2d.left, toScreen2d.top, paint);
                    }
                }
            }

            paint.setColor(Color.argb(80, 255, 255, 255));
            List<Rect> buttonsToDraw = ic.getButtons();
            for (Rect r: buttonsToDraw) {
                RectF rf = new RectF(r.left, r.top, r.right, r.bottom);
                canvas.drawRoundRect(rf, 15f, 15f, paint);
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


    /////////////////On touch event ///////////////////
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (lm != null) {
            ic.handleInput(motionEvent, lm, sm, vp);
        }

        /*switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lm.switchPlayingStatus();
                break;
        }*/
        return true;
    }



}


