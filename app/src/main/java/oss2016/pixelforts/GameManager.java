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
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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
    private String[] playerNames;
    int numPlayers = 2;

    public static DisplayMetrics getMetrics() {return metrics;}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        setContentView(R.layout.activity_game_manager); /* get player info */
    }

    public void playGame(View view){
        /* get player names and number of players */
        if (getPlayerNames(view)) {
            // Create a GLSurfaceVIew instance and set it
            // as the ContentView for this Activity.
            gmView = new GameView(this, this, numPlayers, playerNames);
            onResume(); /* start threads */
            setContentView(gmView);
        }
        else
            Toast.makeText(this, "All active players must have a name", Toast.LENGTH_LONG).show();
    }

    /* Gets the number of active players and their names */
    private boolean getPlayerNames(View view){
        String testName;
        EditText editText;
        CheckBox checkBox;
        String[] temp = new String[5];

        /* player 1*/
        editText = (EditText)findViewById(R.id.editText);
        testName = editText.getText().toString();
        if (testName == null || testName.length() == 0)
            return false;
        temp[0] = testName;

        /* player 2*/
        editText = (EditText)findViewById(R.id.editText2);
        testName = editText.getText().toString();
        if (testName == null || testName.length() == 0)
            return false;
        temp[1] = testName;

        /* player 3*/
        checkBox = (CheckBox)findViewById(R.id.checkBox);
        if (checkBox.isChecked()) {
            ++numPlayers;
            editText = (EditText) findViewById(R.id.editText3);
            testName = editText.getText().toString();
            if (testName == null || testName.length() == 0)
                return false;
            temp[2] = testName;
        }

        /* player 4*/
        checkBox = (CheckBox)findViewById(R.id.checkBox2);
        if (checkBox.isChecked()) {
            ++numPlayers;
            editText = (EditText) findViewById(R.id.editText4);
            testName = editText.getText().toString();
            if (testName == null || testName.length() == 0)
                return false;
            temp[3] = testName;
        }

        /* player 5*/
        checkBox = (CheckBox)findViewById(R.id.checkBox3);
        if (checkBox.isChecked()) {
            ++numPlayers;
            editText = (EditText) findViewById(R.id.editText5);
            testName = editText.getText().toString();
            if (testName == null || testName.length() == 0)
                return false;
            temp[4] = testName;
        }

        /* rebuild the player name array with the exact number of elements */
        if (numPlayers < 5){
            playerNames = new String[numPlayers];
            int j = 0;
            for (int i = 0; i < numPlayers; ++i){
                while(j < 5 && temp[j] == null);
                playerNames[i] = temp[j];
            }
        }
        else
            playerNames = temp;
        return true;
    }



    /* Called by android when the player starts*/
    @Override
    protected void onResume() {
        super.onResume();

        if(gmView != null)
            gmView.resume();
    }

    /* Called by Android when the player change focus from the game */
    @Override
    protected void onPause() {
        super.onPause();

        if(gmView != null)
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
    private Rectangle fireButton;

    public static long FPS(){ return fps; }



    public GameView(Context context, AppCompatActivity appLevel, int numPlayers, String [] playerNames) {
        super(context);
        app = appLevel;
        this.numPlayers = numPlayers;

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        gmRenderer = new GMGLRenderer();

        // Set the Renderer for drawing on the GMGLSurfaceView
        setRenderer(gmRenderer);

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        gameStart(playerNames);
    }

    /* initial game setup */
    private void gameStart(String [] playerNames) {
        players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; ++i)
            players[i] = new Human(playerNames[i]);
        scene = new Scene(players);
        for (int i = 0; i < numPlayers; ++i) {
            players[i].setWeapon(new Weapon(players[i].Fort().CenterX(), players[i].Fort().Top(), 20, .01f));
            players[i].currentWeapon().removeCrosshair();
        }
        currentPlayer = 0;
        setupPlayer = false;
        chargingWeapon = false;
        fire = false;

        /* place fire button */
        fireButton = new Rectangle(-1.55f, -.75f, .5f, .5f );
        fireButton.addRenderer();
        fireButton.setColor(1.0f, .0f, .0f, 0.0f);
        fireButton.setDimensions(.5f, .5f);
        GMGLRenderer.getRenderQueue().Add(fireButton);

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
        if (numPlayers < 2)
            playing = false;  /* game is over */
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
                players[currentPlayer].currentWeapon().activateCrosshair();
                setupPlayer = true;
                fire = false;
                chargingWeapon = false;
            }
            if (chargingWeapon){
                /* full charge in roughly 1 second */
                players[currentPlayer].currentWeapon().charge(1.0f / fps);
            }
            else if (fire){
                Projectile bullet = players[currentPlayer].currentWeapon().fire();
                scene.addActive(bullet);
                GMGLRenderer.getRenderQueue().Add(bullet);
                setupPlayer = false;
                players[currentPlayer].currentWeapon().removeCrosshair();

                ++currentPlayer;
                if (currentPlayer >= numPlayers)
                    currentPlayer = 0;
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
                if (setupPlayer && !chargingWeapon  && (xWorld < -1.45f && yWorld < -.4f))
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

    /* cleans up the threads / scene and returns the start screen */
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