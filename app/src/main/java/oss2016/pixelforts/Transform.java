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
    private boolean isMoving; /* holds if the object has not finished moving */
    private boolean isDead;
    private boolean hasGravity;
    private Collider collider;
    private float velocityX;
    private float velocityY;

    public float CenterX() { return centerX; }
    public float CenterY() { return centerY; }
    public boolean IsMoving() {return  isMoving; }
    public boolean IsDead() { return isDead; }

    /* adds a collider to the object for collision detection */
    public void setCollider(Collider toSet){
        collider = toSet;
        if (collider != null)
        collider.setTransform(this);
    }
    public Collider getCollider(){ return collider;}
    public void setDead(boolean val){
        isDead = val;
    }
    public void setHasGravity(boolean val) { hasGravity = val;}

    public void destroy(){}

    public Transform(){
        centerX = 0.0f;
        centerY = 0.0f;

        isMoving = false;
        isDead = false;
        hasGravity = false;
    }

    public Transform(float X, float Y){
        SetCenter(X, Y);

        isMoving = false;
        isDead = false;
        hasGravity = false;
    }

    /* change center position relative to current position */
    public void ApplyForce(float VelocityX, float VelocityY){
        velocityX += VelocityX;
        velocityY += VelocityY;
        isMoving = true;
    }

    /* handles updating the movement from velocity each frame */
    public void Update(){
        if (isDead) {
            return;
        }

        if (hasGravity)
            ApplyForce(0.0f, -.00002f);
        SetCenter(centerX + velocityX, centerY + velocityY);
    }

    /* called by projectiles on collision */
    public void dealDamage(int damage){}

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
        if(collider.hasCollision(toCheck.collider)) {
            velocityX = 0;
            velocityY = 0;
            isMoving = false;
            return true;
        }
        return false;
    }

    /* build the vertices for derived classes so they can be drawn */
    public abstract void buildVertices();

    /* draw the object */
    public abstract void Draw(float[] mvpMatrix);

    /* adds a renderer to be able to draw the object */
    public abstract int addRenderer();

    /* removes an existing renderer */
    public abstract int removeRenderer();

    /* set the color of the renderer */
    public abstract void setColor(float red, float green, float blue, float alpha);

    /* Returns the bounds of the object */
    public abstract float Top();
    public abstract float Bottom();
    public abstract float Left();
    public abstract float Right();
}
