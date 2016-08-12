package oss2016.pixelforts;

import android.content.Context;;;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   GameManager manages the gameplay loop, including changing players, and calling the renderer.

   All rendering code comes from Google's Android Developer Training
   <https://developer.android.com/training/graphics/opengl/index.html>.
*/
public class GameManager extends AppCompatActivity {
    private GLSurfaceView gmView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceVIew instance and set it
        // as the ContentView for this Activity.
        gmView = new GameView(this);
        setContentView(gmView);
    }
}

class GameView extends GLSurfaceView {
    private final GMGLRenderer gmRenderer;

    private Fort[] players;
    private int numPlayers = 5;
    private Scene scene; /* holds references to all object in the scene */
    private RenderQueue renderQueue;

    private Projectile testParticle;

    public GameView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        gmRenderer = new GMGLRenderer();

        // Set the Renderer for drawing on the GMGLSurfaceView
        setRenderer(gmRenderer);

        gameStart();
    }

    private void gameStart(){
        players = new Fort[numPlayers];
        for (int i = 0; i < numPlayers; ++i)
            players[i] = new Fort();
        scene = new Scene(players);

        testParticle = new Projectile(0.0f, 1.0f, .1f, .1f);
        renderQueue = scene.getRenderQueue();
        renderQueue .Add(testParticle);


        /* start gameloop thread */
        Thread loop = new Thread()
        {
            public void run()
            {
                gameLoop();
            }
        };
        loop.start();
    }

    /* Manages telling projectiles to update and the scene to check for collisions */
    private void gameLoop(){
        while (numPlayers > 1){
            testParticle.Update();
            scene.hasCollisions(testParticle);
            if (testParticle.IsDead())
            {
                renderQueue.remove(testParticle);
            }

            requestRender();
            try {Thread.sleep(50); } catch(Exception e) {}
        }
    }
}