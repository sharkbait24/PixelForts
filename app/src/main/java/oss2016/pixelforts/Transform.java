package oss2016.pixelforts;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

    This is the base class of all of the objects in the game for
    easy distance comparisons for collision detection.
*/
public abstract class Transform {
    private float centerX;
    private float centerY;
    private float top;
    private float bottom;
    private float left;
    private float right;
    private boolean needsUpdate;
    private boolean isMoving; /* holds if the object has not finished moving */

    public float CenterX() { return centerX; }
    public float CenterY() { return centerY; }
    public float Top(){ return top; }
    public float Bottom(){ return bottom;}
    public float Left() { return left; }
    public float Right() { return right; }
    public boolean NeedsUpdate() { return needsUpdate; }
    public boolean IsMoving() {return  isMoving; }

    public Transform(){
        centerX = 0.0f;
        centerY = 0.0f;
        top = 0.0f;
        bottom = 0.0f;
        left = 0.0f;
        right = 0.0f;

        needsUpdate = false;
        isMoving = false;
    }

    public Transform(float X, float Y){
        SetCenter(X, Y);
        top = 0.0f;
        bottom = 0.0f;
        left = 0.0f;
        right = 0.0f;

        needsUpdate = false;
        isMoving = false;
    }

    /* change center position relative to current position */
    public void Move(float deltaX, float deltaY){
        centerX += deltaX;
        centerY += deltaY;
        isMoving = true;
    }

    /* move to a specific position */
    public void SetCenter(float CenterX, float CenterY){
        centerX = CenterX;
        centerY = CenterY;
    }

    /* set the two primary */
    public void setBounds(float Top, float Bottom, float Left, float Right){
        top = Top;
        bottom = Bottom;
        left = Left;
        right = Right;
    }

    /* calculate the distance between the center of this shape and another */
    public float Distance(Transform from){ return Distance(from.centerX, from.centerY); }

    public float Distance(float fromX, float fromY){
        float dx = centerX - fromX;
        float dy = centerY - fromY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /* Set a square's dimensions */
    public boolean setDimensions(float Width, float Height){
        return false; /* if this runs, the object is not a square */
    }

    /* Set a circle's dimensions */
    public boolean setDimensions(float Radius){
        return false;
    }

    /* checks for collision with a Circle object */
    public boolean hasCollision(Circle circle){
        return false;
    }

    /* checks for collision with a Square object */
    public boolean hasCollision(Rectangle rectangle){
        return false;
    }

    /* build the vertices for derived classes so they can be drawn */
    public abstract void buildVertices();

    /* draw the object */
    public abstract void Draw(float[] mvpMatrix);

    /* Returns if the object needs to be drawn */
    public abstract boolean NeedsRedrawn();

    /* adds a renderer to be able to draw the object */
    public abstract int addRenderer();

    /* removes an existing renderer */
    public abstract int removeRenderer();
}
