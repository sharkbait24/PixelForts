package oss2016.pixelforts;

import android.opengl.GLES20;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   The rectangle class will be used for all of the land pieces in the game
 */
public class Rectangle extends Transform {
    private float width;
    private float height;
    private RectangleRenderer renderer;
    private Collider collider;

    public Rectangle() {
        super();

        setDimensions(1.0f, 1.0f);
    }

    public Rectangle(float CenterX, float CenterY, float Width, float Height){
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
        renderer = new RectangleRenderer();
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
        if (Height > 0.0f && Width > 0.0f) {
            width = Width;
            height = Height;

            if (renderer != null){
                renderer.setDimensions(CenterX(), CenterY(), Width, Height);
                renderer.buildVertices();
            }
            if (collider != null)
                collider.setBounds(Width, Height);
            return true;
        }
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
        setDimensions(width, height);
    }

    /* Returns the bounds of the object */
    public float Top(){
        if (collider != null)
            return collider.Top();
        return CenterY() + height / 2.0f;
    }
    public float Bottom(){
        if (collider != null)
            return collider.Bottom();
        return CenterY() - height / 2.0f;
    }
    public float Left(){
        if (collider != null)
            return collider.Left();
        return CenterX() - width / 2.0f;
    }
    public float Right(){
        if (collider != null)
            return collider.Right();
        return CenterX() + width / 2.0f;
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
        glProgram = GMGLRenderer.getGlProgram();
        super.Draw(mvpMatrix, POINTS_PER_VERTEX, glProgram, vertexStride, vertexCount,
                GLES20.GL_TRIANGLE_FAN);
    }
}