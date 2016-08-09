package oss2016.pixelforts;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information..
 */
public class Circle extends Shape{
    float radius;

    public Circle(){
        super();

        radius = 1.0f;
    }

    public Circle(float CenterX, float CenterY, float Radius){
        super(CenterX, CenterY);

        if (!setDimensions(Radius))
        {
            radius = 1.0f;
        }
        buildVertices();
    }

    /* Set a circle's dimensions */
    public boolean setDimensions(float Radius){
        if (radius > 0.0f){
            radius = Radius;
            return true;
        }
        return false;
    }

    /* Build the vertexBuffer and drawListBuffer to be used in the Draw function */
    public void buildVertices(){

    }

    /* Draw the Square */
    public void Draw(){

    }

    /* checks for collision with a Circle object */
    public boolean hasCollision(Circle circle){
        return false;
    }

    /* checks for collision with a Square object */
    public boolean hasCollision(Square square){
        return false;
    }
}
