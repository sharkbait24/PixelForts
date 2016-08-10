package oss2016.pixelforts;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information..
 */
public class Circle extends Transform {
    float radius;
    private boolean needsRedrawn;

    public boolean NeedsRedrawn() { return needsRedrawn; }

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

            buildVertices();
            setBounds();
            return true;
        }
        return false;
    }

    /* Build the vertexBuffer and drawListBuffer to be used in the Draw function */
    public void buildVertices(){
        needsRedrawn = true;
    }

    /* Draw the Square */
    public void Draw(float[] mvpMatrix){
        needsRedrawn = false;
    }

    /* checks for collision with a Circle object */
    public boolean hasCollision(Circle circle){
        return false;
    }

    /* checks for collision with a Square object */
    public boolean hasCollision(Rectangle rectangle){
        return false;
    }

    /* Update the transform's bounds */
    public void setBounds(){
        super.setBounds(CenterY() + radius, CenterY() - radius, CenterX() - radius, CenterX() + radius);
    }

    /* adds a renderer to be able to draw the object */
    public int addRenderer(){
        return 1;
    }

    /* removes an existing renderer */
    public int removeRenderer(){
        return 1;
    }
}