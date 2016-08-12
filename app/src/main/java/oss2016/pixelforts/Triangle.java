package oss2016.pixelforts;

import android.opengl.GLES20;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   The triangle class is not currently implemented.
*/
public class Triangle extends Transform{
    private float[] offsets = { /* x,y distances from center in counterclockwise order */
            0.0f, 0.5f,         /* top */
            -0.5f, 0.0f,        /* bottom left */
            .05f, 0.0f          /* bottom right */
    };
    private Collider collider;
    private TriangleRenderer renderer;

    public Triangle() {
        super();

        setDimensions(1.0f, 1.0f);
    }

    public Triangle(float CenterX, float CenterY, float Width, float Height){
        super(CenterX, CenterY);

        if (!setDimensions(Width, Height))
        { /* use default xyCords values */
            setDimensions(1.0f, 1.0f);
        }
    }

    /* adds a renderer to be able to draw the rectangle */
    public int addRenderer(){
        if (renderer != null)
            return 0;
        renderer = new TriangleRenderer();
        buildVertices();
        return 1;
    }

    /* removes an existing renderer */
    public int removeRenderer(){
        if (renderer == null)
            return 0;
        renderer = null;
        return 1;
    }

    /* set the color of the renderer */
    public void setColor(float red, float green, float blue, float alpha){
        if (renderer != null) {
            float[] temp = {red, green, blue, alpha};
            renderer.setColor(temp);
        }
    }

    /* adds a collider to the object for collision detection */
    public void setCollider(Collider toSet){
        super.setCollider(toSet);
        collider = toSet;
    }

    /* Sets the width and height */
    public boolean setDimensions(float Width, float Height){
        return false;
    }

    /* Build the vertexBuffer and drawListBuffer to be used in the Draw function */
    public void buildVertices(){
        if (renderer != null) {
            renderer.buildVertices();
        }
    }

    /* Draw the Square */
    public void Draw(float[] mvpMatrix){
        if (renderer != null) {
            renderer.Draw(mvpMatrix);
        }
    }

    /* sadly right now setDimensions is the best way to update everything */
    public void Update(){
        super.Update();
    }
    /* Returns the bounds of the object */
    public float Top(){
        if (collider != null)
            return collider.Top();

        return CenterY();
    }
    public float Bottom(){
        if (collider != null)
            return collider.Bottom();
        return CenterY();
    }
    public float Left(){
        if (collider != null)
            return collider.Left();
        return CenterX();
    }
    public float Right(){
        if (collider != null)
            return collider.Right();
        return CenterX();
    }
}

class TriangleRenderer extends ObjectRenderer{
    static final int POINTS_PER_VERTEX = 2;
    static float triangleCords[] = { /* counterclockwise order */
            0.0f, 0.5f, 0.0f,
            -0.5f, 0.0f, 0.0f,
            0.5f, 0.0f, 0.0f
    };
    private short drawOrder[] = {0,1,2};

    private static int vertexCount;
    private static int vertexStride;
    private static int glProgram;

    public TriangleRenderer() {
        vertexCount = triangleCords.length / POINTS_PER_VERTEX;
        vertexStride = POINTS_PER_VERTEX * 4;
        glProgram = GMGLRenderer.getGlProgram();
    }

    public void setDimensions(float CenterX, float CenterY, float Width, float Height){
    }

    public void buildVertices(){
        super.buildVertices(triangleCords, drawOrder);
    }

    public void Draw(float [] mvpMatrix){
        glProgram = GMGLRenderer.getGlProgram();
        super.Draw(mvpMatrix, POINTS_PER_VERTEX, glProgram, vertexStride, vertexCount,
                GLES20.GL_TRIANGLES);
    }
}
