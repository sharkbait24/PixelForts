package oss2016.pixelforts;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   The square class will be used for all of the land and fort objects
   in the game.  General collision detection and rendering is done in this
   class.

   All of the rendering code was taken from Google's Android Developer Training
   <https://developer.android.com/training/graphics/opengl/index.html>.
 */
public class Square extends Shape {
    private float width;
    private float height;

    /* for OpenGL rendering */
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    static final int COORDS_PER_VERTEX = 3; /* OpenGL renders in 3D, but only the first two are used in this game */
    private float squareCoords[] = {
            -0.5f, 0.5f, 0.0f,      /* top left */
            -0.5f, -0.5f, 0.0f,     /* bottom left */
            0.5f, -0.5f, 0.0f,      /* bottom right */
            0.5f, 0.5f, 0.0f };     /* top right */
    private short drawOrder[] = {0, 1, 2, 0, 2, 3};

    public Square() {
        super();

        width = 1.0f;
        height = 1.0f;
    }

    public Square(float CenterX, float CenterY, float Width, float Height){
        super(CenterX, CenterY);

        if (!setDimensions(Width, Height))
        { /* use default squareCoords values */
            width = 1.0f;
            height = 1.0f;
        }
        buildVertices();
    }

    /* Sets the width and height */
    public boolean setDimensions(float Width, float Height){
        if (Height > 0.0f && Width > 0.0f) {
            width = Width;
            height = Height;

            /* change squareCoords (indices 2,5,8,11 are for the z axis which is always 0) */
            float dx = Width / 2.0f;
            float dy = Height / 2.0f;
            squareCoords[0] = squareCoords[3] = -dx;
            squareCoords[6] = squareCoords[9] = dx;
            squareCoords[1] = squareCoords[10] = dy;
            squareCoords[4] = squareCoords[7] = -dy;
            return true;
        }
        return false;
    }

    /* Build the vertexBuffer and drawListBuffer to be used in the Draw function */
    public void buildVertices(){
        /* initialize vertex byte buffer for shape coordinates (# of coordinate values * 4 bytes per float) */
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
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

    /* checks for collision with a Circle object */
    public boolean hasCollision(Circle circle){
        return false;
    }

    /* checks for collision with a Square object */
    public boolean hasCollision(Square square) {
        return false;
    }
}
