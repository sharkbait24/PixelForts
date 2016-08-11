package oss2016.pixelforts;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   The rectangle class will be used for all of the land and fort objects
   in the game.

   All of the rendering code was taken from Google's Android Developer Training
   <https://developer.android.com/training/graphics/opengl/index.html>.
 */
public class Rectangle extends Transform {
    private float width;
    private float height;
    private RectangleRenderer renderer;
    private boolean needsRedrawn;

    public boolean NeedsRedrawn() { return needsRedrawn; }

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
        needsRedrawn = false;
        return 1;
    }

    /* Sets the width and height */
    public boolean setDimensions(float Width, float Height){
        if (Height > 0.0f && Width > 0.0f) {
            width = Width;
            height = Height;

            if (renderer != null){
                renderer.setDimensions(CenterX(), CenterY(), Width, Height);
                renderer.buildVertices();
                needsRedrawn = true;
            }
            setBounds(); /* update transform's bounds */
            return true;
        }
        return false;
    }

    /* Build the vertexBuffer and drawListBuffer to be used in the Draw function */
    public void buildVertices(){
        if (renderer != null) {
            renderer.buildVertices();
            needsRedrawn = true;
        }
    }

    /* Draw the Square */
    public void Draw(float[] mvpMatrix){
        if (renderer != null) {
            renderer.Draw(mvpMatrix);
            needsRedrawn = !IsMoving(); /* if still moving this needs to be rendered again next frame */
        }
        else
            needsRedrawn = false;
    }

    /* checks for collision with a Circle object */
    public boolean hasCollision(Circle circle){
        return false;
    }

    /* checks for collision with a Square object */
    public boolean hasCollision(Rectangle rectangle) {
        return false;
    }

    /* Update transform's bounds (top, bottom, left, right)
    *  This is only used if there is no collider */
    private void setBounds(){
        float dx = width / 2.0f;
        float dy = height / 2.0f;
        super.setBounds(CenterY() + dy, CenterY() - dy, CenterX() - dx, CenterX() + dx);
    }
}


