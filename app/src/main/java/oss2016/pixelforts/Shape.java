package oss2016.pixelforts;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

    This is the base class of all of the objects in games
    so that they can easily determine distance from each other
    for collision detection.
*/
public abstract class Shape {
    protected float centerX;
    protected float centerY;

    public Shape(){
        centerX = 0.0f;
        centerY = 0.0f;
    }

    public Shape(float X, float Y){
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
    public float Distance(Shape shape){
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
    public void buildVertices(){}

    /* draw the object */
    public abstract void Draw();

    /* checks for collision with a Circle object */
    public abstract boolean hasCollision(Circle circle);

    /* checks for collision with a Square object */
    public abstract boolean hasCollision(Square square);
}
