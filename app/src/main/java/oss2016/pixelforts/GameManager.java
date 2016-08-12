package oss2016.pixelforts;

import android.content.Context;;;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   GameManager starts the gameView scene which updates the game until a winner is found.

   Most of the rendering code comes from Google's Android Developer Training
   <https://developer.android.com/training/graphics/opengl/index.html>.
*/
public class GameManager extends AppCompatActivity {
    private GameView gmView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceVIew instance and set it
        // as the ContentView for this Activity.
        gmView = new GameView(this);
        setContentView(gmView);
    }

    /* Called by android when the player starts*/
    @Override
    protected void onResume() {
        super.onResume();

        gmView.resume();
    }

    /* Called by Android when the player change focus from the game */
    @Override
    protected void onPause() {
        super.onPause();

        gmView.pause();
    }
}
/* The main game thread.  The GLSurfaceView is used to render objects and detect touch events.
    The Runnable, allows us to define our own game thread that will run the main game loop that
    updates the game.

    Most of the game loop and GameView logic came from a web tutorial by John Horton
    <http://gamecodeschool.com/android/building-a-simple-game-engine/>
 */
class GameView extends GLSurfaceView implements Runnable{
    private final GMGLRenderer gmRenderer;
    private Thread gameLoop = null;
    volatile boolean playing;
    private static final int fpsCap = 30; /* desired maximum frame rate in seconds*/
    private static final long fpsCapMillis = 1000 / fpsCap;
    private static long fps; /* frame rate for update loop*/
    private long timeThisFrame; /* used to limit that rate update is called */

    private Fort[] players;
    private int numPlayers = 5;
    private Scene scene; /* holds references to all object in the scene */
    private RenderQueue renderQueue;

    private Projectile testParticle;

    public static long FPS(){ return fps; }

    public GameView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        gmRenderer = new GMGLRenderer();

        // Set the Renderer for drawing on the GMGLSurfaceView
        setRenderer(gmRenderer);

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        gameStart();
    }

    /* initial game setup */
    private void gameStart() {
        players = new Fort[numPlayers];
        for (int i = 0; i < numPlayers; ++i)
            players[i] = new Fort();
        scene = new Scene(players);

        /* allows the run() thread method to update the game */
        playing = true;
    }

    /* called as part of the Thread class by Android and performs the main game loop updating
    * and rendering. */
    @Override
    public void run() {
        long startFrameTime;
        fps = 1000 / fpsCapMillis; /* default to desired frame rate */
        long sleepTime;
        while (playing){
            startFrameTime = SystemClock.uptimeMillis();

            update();

            /* asks GMGLRenderer to run onDraw */
            requestRender();

            timeThisFrame = SystemClock.uptimeMillis() - startFrameTime;
            if (timeThisFrame > 0) {
                fps = 1000 / timeThisFrame;
                sleepTime = fpsCapMillis - timeThisFrame;
                try {
                    if (sleepTime > 0)
                        Thread.sleep(sleepTime);
                } catch (Exception e) {}
            }
        }
    }

    /* Run the scene while there are active objects in it.
        If the scene is quiet, have the next player play*/
    public void update() {
        if (numPlayers < 2)
            playing = false;  /* game is over */

        else if (scene.isActive()){ /* keep running the scene until all motion has stopped */
            scene.update();
        }

        else{ /* player logic */

        }
    }

    /*User left the game, close the gameLoop thread */
    public void pause() {
        playing = false;
        try {
            gameLoop.join();
        } catch (InterruptedException e){
            Log.e("Error:", "joining thread");
        }
    }

    /* The GameManager has started (restarted) */
    public void resume() {
        playing = true;
        gameLoop = new Thread(this);
        gameLoop.start();
    }
}