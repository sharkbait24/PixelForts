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
    private Fort[] players;
    private Scene scene; /* holds references to all object in the scene */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceVIew instance and set it
        // as the ContentView for this Activity.
        gmView = new GMGLSurfaceView(this);
        setContentView(gmView);

        scene = new Scene(players);
    }
}

class GMGLSurfaceView extends GLSurfaceView {
    private final GMGLRenderer gmRenderer;

    public GMGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        gmRenderer = new GMGLRenderer();

        // Set the Renderer for drawing on the GMGLSurfaceView
        setRenderer(gmRenderer);
    }
}