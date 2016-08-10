package oss2016.pixelforts;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

    This is the base class of all of the objects in the game for
    easy distance comparisons for collision detection.
*/
public abstract class Transform {
    protected float centerX;
    protected float centerY;
    protected float top;
    protected float bottom;

    public float getTop(){ return top; }
    public float getBottom(){ return bottom;}

    public Transform(){
        centerX = 0.0f;
        centerY = 0.0f;
        top = 1.0f;
        bottom = 1.0f;
    }

    public Transform(float X, float Y){
        MoveTo(X, Y);
    }

    /* move relative to current position */
    public void Move(float deltaX, float deltaY){
        centerX += deltaX;
        centerY += deltaY;
    }

    /* move to a specific position */
    public void MoveTo(float CenterX, float CenterY){
        centerX = CenterX;
        centerY = CenterY;
    }

    /* calculate the distance between the center of this shape and another */
    public float Distance(Transform shape){
        return Distance(shape.centerX, shape.centerY);
    }

    public float Distance(float fromX, float fromY){
        float dx = centerX - fromX;
        float dy = centerY - fromY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /* Set a square's dimensions */
    public boolean setDimensions(float Width, float Height){
        return false; /* if this is run object is not a square */
    }

    /* Set a circle's dimensions */
    public boolean setDimensions(float Radius){
        return false;
    }

    /* build the vertices for derived classes so they can be drawn */
    public abstract void buildVertices();

    /* draw the object */
    public abstract void Draw();

    /* checks for collision with a Circle object */
    public abstract boolean hasCollision(Circle circle);

    /* checks for collision with a Square object */
    public abstract boolean hasCollision(Rectangle rectangle);

    /* require any derived class to properly update the top and bottom values */
    public abstract void setTopAndBottom();
}
