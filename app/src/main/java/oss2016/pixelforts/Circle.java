package oss2016.pixelforts;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information..
 */
public class Circle extends Transform {
    float radius;
    private boolean needsRedrawn;
    private Collider collider;

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
            if (collider != null)
                collider.setBounds(Radius);
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

    /* adds a renderer to be able to draw the object */
    public int addRenderer(){
        return 0;
    }

    /* removes an existing renderer */
    public int removeRenderer(){
        return 0;
    }

    /* set the color of the renderer */
    public void setColor(float red, float green, float blue, float alpha){

    }

    /* adds a collider to the object for collision detection */
    public void setCollider(Collider toSet){
        super.setCollider(toSet);
        collider = toSet;
    }

    /* Returns the bounds of the object */
    public float Top(){
        if (collider != null)
            return collider.Top();
        return CenterY() + radius;
    }
    public float Bottom(){
        if (collider != null)
            return collider.Bottom();
        return CenterY() - radius;
    }
    public float Left(){
        if (collider != null)
            return collider.Left();
        return CenterX() - radius;
    }
    public float Right(){
        if (collider != null)
            return collider.Right();
        return CenterY() + radius;
    }
}