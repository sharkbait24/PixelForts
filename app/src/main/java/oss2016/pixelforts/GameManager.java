package oss2016.pixelforts;

import android.app.Application;
import android.content.Context;;;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   GameManager starts the gameView scene which updates the game until a winner is found.

   Most of the rendering code comes from Google's Android Developer Training
   <https://developer.android.com/training/graphics/opengl/index.html>.
*/
public class GameManager extends AppCompatActivity {
    private GameView gmView;
    private static DisplayMetrics metrics = new DisplayMetrics();

    public static DisplayMetrics getMetrics() {return metrics;}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Create a GLSurfaceVIew instance and set it
        // as the ContentView for this Activity.
        gmView = new GameView(this, this);
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
    private final AppCompatActivity app;
    private final GMGLRenderer gmRenderer;
    private Thread gameLoop = null;
    volatile boolean playing;
    volatile boolean leaveGame; /* used to determine when the player is ready to leave the game */
    private static final int fpsCap = 30; /* desired maximum frame rate in seconds*/
    private static final long fpsCapMillis = 1000 / fpsCap;
    private static long fps; /* frame rate for update loop*/
    private long timeThisFrame; /* used to limit that rate update is called */

    private Player[] players;
    private int numPlayers = 5;
    private Scene scene; /* holds references to all object in the scene */
    private int currentPlayer;
    private boolean setupPlayer; /* holds if the player has been setup after switching players */
    private boolean chargingWeapon;
    private boolean fire;

    public static long FPS(){ return fps; }



    public GameView(Context context, AppCompatActivity appLevel) {
        super(context);
        app = appLevel;

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
        players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; ++i)
            players[i] = new Human("Player " + (i + 1));
        scene = new Scene(players);
        currentPlayer = 0;
        setupPlayer = false;
        chargingWeapon = false;
        fire = false;

        /* allows the run() thread method to update the game */
        playing = true;
        leaveGame = false;
    }

    /* called as part of the Thread class by Android and performs the main game loop updating
    * and rendering. */
    @Override
    public void run() {
        long startFrameTime;
        fps = 1000 / fpsCapMillis; /* default to desired frame rate */
        long sleepTime;
        while (!leaveGame){
            startFrameTime = SystemClock.uptimeMillis();

            update();

            /* asks gmRenderer to run onDraw */
            requestRender();

            timeThisFrame = SystemClock.uptimeMillis() - startFrameTime;
            if (timeThisFrame > 0) {
                fps = 1000 / timeThisFrame;
                sleepTime = fpsCapMillis - timeThisFrame;
                try {
                    if (sleepTime > 0)
                        Thread.sleep(sleepTime);
                    else
                        Thread.sleep(10);
                } catch (Exception e) {}
            }
        }
    }

    /* Run the scene while there are active objects in it.
        If the scene is quiet, have the next player play*/
    public void update() {
        if (numPlayers < 2) {

            playing = false;  /* game is over */
            scene.destroyScene();
        }
        else if (scene.isActive()){ /* keep running the scene until all motion has stopped */
            scene.update();

            /* check for dead players */
            for (int i = 0; i < numPlayers; ++i){
                if(players[i].IsDead()){
                    /* shift down */
                    Player temp = players[i];
                    for (int j = i + 1; j < numPlayers; ++j)
                    {
                        players[j-1] = players[j];
                    }
                    players[numPlayers - 1] = temp;
                    scene.removeFort(temp.Fort());
                    --numPlayers;

                    if (currentPlayer > i){
                        --currentPlayer;
                    }
                    --i; /* current i index is a different player now need to check them */
                }
            }
            if (currentPlayer >= numPlayers)
                currentPlayer = 0;
        }

        else{ /* player logic */
            if (!setupPlayer){
                Fort playerFort = players[currentPlayer].Fort();
                players[currentPlayer].setWeapon(new Weapon(playerFort.CenterX(), playerFort.Top(), 20, .01f));
                setupPlayer = true;
                fire = false;
                chargingWeapon = false;
            }
            if (chargingWeapon){
                /* full charge in roughly 1 second */
                players[currentPlayer].currentWeapon().charge(4.0f / fps);
            }
            else if (fire){
                Projectile bullet = players[currentPlayer].currentWeapon().fire();
                scene.addActive(bullet);
                GMGLRenderer.getRenderQueue().Add(bullet);

                players[currentPlayer].destroyWeapon();
                ++currentPlayer;
                if (currentPlayer >= numPlayers)
                    currentPlayer = 0;

                setupPlayer = false;
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){

        float x = motionEvent.getX();
        float y = motionEvent.getY();

        /* change to world coords */
        float width = (float) GameManager.getMetrics().widthPixels;
        float height = (float) GameManager.getMetrics().heightPixels;
        float xWorld = 4.0f * (width / 2.0f - x) / width;
        float yWorld = 2.0f * (height / 2.0f - y) / height;

        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                /* Check if we pressed the fire button */
                if (!chargingWeapon  && (xWorld < -1.7f && yWorld < -.7f))
                    chargingWeapon = true;

                /* final cleanup */
                if(!playing){
                    exit();
                }

                /* else fall through to the next case */
            case MotionEvent.ACTION_MOVE:
                if (!chargingWeapon) {
                    if (players[currentPlayer].currentWeapon() != null)
                        players[currentPlayer].currentWeapon().aim(xWorld, yWorld);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (chargingWeapon) {
                    fire = true;
                    chargingWeapon = false;
                }
        }
        return true;
    }

    public void exit(){
        pause();
        scene.destroyScene();
        app.finish();
    }

    /*User left the game, close the gameLoop thread */
    public void pause() {
        super.onPause(); /* GLSurfaceView's onPause that stops the OpenGL Rendering thread */

        playing = false;
        leaveGame = true;
        try {
            gameLoop.join();
        } catch (InterruptedException e){
            Log.e("Error:", "joining thread");
        }
    }

    /* The GameManager has started (restarted) */
    public void resume() {
        super.onResume(); /* GLSurfaceView's onResume that starts the OpenGL thread */

        playing = true;
        leaveGame = false;
        gameLoop = new Thread(this);
        gameLoop.start();
    }
}