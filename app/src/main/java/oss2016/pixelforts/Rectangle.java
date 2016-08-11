package oss2016.pixelforts;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   The rectangle class will be used for all of the land pieces in the game
 */
public class Rectangle extends Transform {
    private float width;
    private float height;
    private RectangleRenderer renderer;
    private boolean needsRedrawn;
    private Collider collider;

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
                needsRedrawn = true;
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
        return CenterY() + width / 2.0f;
    }
}