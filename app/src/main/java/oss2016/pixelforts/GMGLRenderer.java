package oss2016.pixelforts;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   Handles the Rendering of objects using a single vertex and fragment shader
   This class performs the camera position calculations and generates the
   MVP matrix that is used for all of the object's draw calls.  The drawing of
   each frame is also handled here.

   All rendering code comes from Google's Android Developer Training
   <https://developer.android.com/training/graphics/opengl/index.html>.
*/
public class GMGLRenderer implements GLSurfaceView.Renderer{
    private static RenderQueue renderQueue;
    private static int vertexShader;
    private static int fragmentShader;
    private static int glProgram; /* attaches vertex and fragment shaders and is used to render objects */

    public static int getGlProgram() {return glProgram; }
    public static RenderQueue getRenderQueue() {return renderQueue; }

    /* mMVPMatrix is an abbreviation for "Model View Projection Matrix" */
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    /* generic shader code for all objects */
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * vPosition;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "   gl_FragColor = vColor;" +
                    "}";

    public GMGLRenderer(){
        super();
        renderQueue = new RenderQueue(mMVPMatrix);
    }

    /* loads and compiles a shader to be used in an OpenGL environment */
    public static int loadShader(int type, String shaderCode){
        /* create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
         */
        int shader = GLES20.glCreateShader(type);

        /* add the source code to the shader and compile it */
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        glProgram = GLES20.glCreateProgram();               /* create empty OpenGL ES Program */
        GLES20.glAttachShader(glProgram, vertexShader);     /* add the vertex shader to program */
        GLES20.glAttachShader(glProgram, fragmentShader);   /* add fragment shader */
        GLES20.glLinkProgram(glProgram);                    /* creates OpenGL ES program executables */
    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        /* Set the camera position (View matrix) */
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        /* Calculate the projection and view transformation */
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        /* Draw all objects in the queue */
        renderQueue.DrawAll();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        /* this projection matrix is applied to object coordinates
           in the OnDrawFrame() method
        */
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }
}