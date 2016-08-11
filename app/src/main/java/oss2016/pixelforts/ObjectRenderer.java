package oss2016.pixelforts;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   The generic object renderer that renders objects by a vertex buffer that makes
   triangles with points in a counter clockwise order.

   All of the rendering code was taken from Google's Android Developer Training
   <https://developer.android.com/training/graphics/opengl/index.html>.
 */
public abstract class ObjectRenderer {
    /* for OpenGL rendering */
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private float [] color = {0.5f, 0.5f, 0.5f, 1.0f}; /* R,G,B,A */

    /* handles to variables in the shader code */
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    /* Red, green, blue and alpha (opacity) values */
    public void setColor(float [] Color){
        if (Color == null)
            return;

        for (int i = 0; i < Color.length && i < 4; ++i)
            color[i] = Color[i];
    }

    /* Build the vertexBuffer and drawListBuffer to be used in the Draw function */
    public void buildVertices(float [] xyCords, short [] drawOrder){
        /* initialize vertex byte buffer for shape coordinates (# of coordinate values * 4 bytes per float) */
        ByteBuffer bb = ByteBuffer.allocateDirect(xyCords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(xyCords);
        vertexBuffer.position(0);

        /* initialize byte buffer for the draw list (# coords * 2 bytes per short) */
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
    }

    /* Derived class will use this to call the next draw with the necessary info */
    public abstract void Draw(float [] mvpMatrix);

    /* Draw the Square */
    public void Draw(float[] mvpMatrix, int POINTS_PER_VERTEX, int glProgram, int vertexStride,
                     int vertexCount){
        /* Add program to OpenGL ES environment */
        GLES20.glUseProgram(glProgram);

        /* get handle to vertex shader's vPosition member */
        mPositionHandle = GLES20.glGetAttribLocation(glProgram, "vPosition");

        /* Enable a handle to the 2 triangle vertices (rectangle rendered as 2 triangles) */
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        /* Prepare the 2 triangle's coordinate data */
        GLES20.glVertexAttribPointer(mPositionHandle, POINTS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        /* get handle to fragment shader's vColor member */
        mColorHandle = GLES20.glGetUniformLocation(glProgram, "vColor");

        /* set color for drawing the triangles */
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        /* Get handle to shape's transformation matrix */
        mMVPMatrixHandle = GLES20.glGetUniformLocation(glProgram, "uMVPMatrix");

        /* Pass the projection and view transformation to the shader */
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        /* Draw the triangles */
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);

        /* Disable vertex array */
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}

/* Handles the vertex points and rendering for the Rectangle class*/
class RectangleRenderer extends ObjectRenderer{
    static final int POINTS_PER_VERTEX = 3; /* OpenGL renders in 3D, but only the first two are used in this game */
    private final short drawOrder[] = {0, 1, 2, 0, 2, 3}; /* counter clockwise triangles */
    private float xyCords[] = {
            -0.5f, 0.5f, 0.0f,      /* top left */
            -0.5f, -0.5f, 0.0f,     /* bottom left */
            0.5f, -0.5f, 0.0f,      /* bottom right */
            0.5f, 0.5f, 0.0f };     /* top right */

    private static int vertexCount;
    private static int vertexStride;
    private static int glProgram;

    public RectangleRenderer() {
        vertexCount = xyCords.length / POINTS_PER_VERTEX;
        vertexStride = POINTS_PER_VERTEX * 4;
        glProgram = GMGLRenderer.getGlProgram();
    }

    public void setDimensions(float CenterX, float CenterY, float Width, float Height){
        if (Height > 0.0f && Width > 0.0f) {
            /* change xyCords (indices 2,5,8,11 are for the z axis which is always 0)
            * Note: rotations are not currently supported*/
            float dx = Width / 2.0f;
            float dy = Height / 2.0f;
            xyCords[0] = xyCords[3] = CenterX - dx;
            xyCords[6] = xyCords[9] = CenterX + dx;
            xyCords[1] = xyCords[10] = CenterY + dy;
            xyCords[4] = xyCords[7] = CenterY - dy;
        }
    }

    public void buildVertices(){
        super.buildVertices(xyCords, drawOrder);
    }

    public void Draw(float [] mvpMatrix){
        super.Draw(mvpMatrix, POINTS_PER_VERTEX, glProgram, vertexStride, vertexCount);
    }


}