package oss2016.pixelforts;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   The rectangle class will be used for all of the land and fort objects
   in the game.  General collision detection and transformations done in this class.

   All of the rendering code was taken from Google's Android Developer Training
   <https://developer.android.com/training/graphics/opengl/index.html>.
 */
public class Rectangle extends Transform {
    private float width;
    private float height;
    private RectangleRenderer renderer;

    static final int COORDS_PER_VERTEX = 3; /* OpenGL renders in 3D, but only the first two are used in this game */
    private float xyCords[] = {
            -0.5f, 0.5f, 0.0f,      /* top left */
            -0.5f, -0.5f, 0.0f,     /* bottom left */
            0.5f, -0.5f, 0.0f,      /* bottom right */
            0.5f, 0.5f, 0.0f };     /* top right */

    public Rectangle() {
        super();

        width = 1.0f;
        height = 1.0f;
        renderer = new RectangleRenderer();
    }

    public Rectangle(float CenterX, float CenterY, float Width, float Height){
        super(CenterX, CenterY);

        if (!setDimensions(Width, Height))
        { /* use default xyCords values */
            width = 1.0f;
            height = 1.0f;
        }
        renderer = new RectangleRenderer();
        buildVertices();
    }

    /* adds a renderer to be able to draw the rectangle */
    public int addRenderer(){
        if (renderer != null)
            return 0;
        renderer = new RectangleRenderer();
        return 1;
    }

    /* removes an existing renderer */
    public int removeRenderer(){
        if (renderer == null)
            return 0;
        renderer = null;
        return 1;
    }

    /* Sets the width and height */
    public boolean setDimensions(float Width, float Height){
        if (Height > 0.0f && Width > 0.0f) {
            width = Width;
            height = Height;

            /* change xyCords (indices 2,5,8,11 are for the z axis which is always 0)
            * Note rotations are not currently supported*/
            float dx = Width / 2.0f;
            float dy = Height / 2.0f;
            xyCords[0] = xyCords[3] = -dx;
            xyCords[6] = xyCords[9] = dx;
            xyCords[1] = xyCords[10] = dy;
            xyCords[4] = xyCords[7] = -dy;

            setTopAndBottom();
            return true;
        }
        return false;
    }

    /* Build the vertexBuffer and drawListBuffer to be used in the Draw function */
    public void buildVertices(){
        if (renderer != null)
            renderer.buildVertices(xyCords);
    }

    /* Draw the Square */
    public void Draw(){
        if (renderer != null)
            renderer.Draw();
    }

    /* checks for collision with a Circle object */
    public boolean hasCollision(Circle circle){
        return false;
    }

    /* checks for collision with a Square object */
    public boolean hasCollision(Rectangle rectangle) {
        return false;
    }

    /* Update the top and bottom to the appropriate corner of the rectangle
    *  Note that this currently does not support rotations. */
    public void setTopAndBottom(){
        top = xyCords [1];
        bottom = xyCords [4];
    }
}

/* Handles the vertex points and rendering for the Rectangle class*/
class RectangleRenderer{
    /* for OpenGL rendering */
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private short drawOrder[] = {0, 1, 2, 0, 2, 3};

    int test = GMGLRenderer.getGlProgram();

    /* Build the vertexBuffer and drawListBuffer to be used in the Draw function */
    public void buildVertices(float [] xyCords){
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

    /* Draw the Square */
    public void Draw(){

    }
}
