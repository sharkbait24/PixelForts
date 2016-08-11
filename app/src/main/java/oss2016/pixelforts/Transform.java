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
    private boolean needsUpdate;
    private boolean isMoving; /* holds if the object has not finished moving */
    private Collider collider;

    public float CenterX() { return centerX; }
    public float CenterY() { return centerY; }
    public boolean NeedsUpdate() { return needsUpdate; }
    public boolean IsMoving() {return  isMoving; }

    /* adds a collider to the object for collision detection */
    public void setCollider(Collider toSet){
        collider = toSet;
    }
    public Collider getCollider(){ return collider;}

    public Transform(){
        centerX = 0.0f;
        centerY = 0.0f;

        needsUpdate = false;
        isMoving = false;
    }

    public Transform(float X, float Y){
        SetCenter(X, Y);

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



    /* checks for collision with another transform's collider */
    public boolean hasCollision(Transform toCheck){
        if (collider == null || toCheck.collider == null)
            return false;
        return collider.hasCollision(toCheck.collider);
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

    /* Returns the bounds of the object */
    public abstract float Top();
    public abstract float Bottom();
    public abstract float Left();
    public abstract float Right();
}
