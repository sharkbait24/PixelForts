package oss2016.pixelforts;

import android.app.WallpaperInfo;

import java.util.Vector;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   The collider is the base class for collision detection in the game which is necessary
   for the physics movement.

   General idea for the collision detection came from the Mozilla Developer Network
   <https://developer.mozilla.org/en-US/docs/Games/Techniques/2D_collision_detection>
 */
public abstract class Collider {
    protected Transform transform;
    private float top; /* used for the general "broad" collision detection */
    private float bottom;
    private float left;
    private float right;

    /* Returns the bounds of the object */
    public float Top(){ return top;}
    public float Bottom(){ return bottom;}
    public float Left(){ return left;}
    public float Right(){ return right;}

    public void setTransform(Transform transform){
        this.transform = transform;
    }

    /* checks for collision with another collider */
    public boolean hasCollision(Collider toCheck){
        if (toCheck == null)
            return false;

        if (toCheck instanceof BoxCollider)
            return hasCollision((BoxCollider) toCheck);
        else if (toCheck instanceof CircleCollider)
            return hasCollision((CircleCollider) toCheck);
        else if (toCheck instanceof CompoundCollider)
            return hasCollision((CompoundCollider) toCheck);
        return false;
    }

    /* checks for collision with a BoxCollider */
    public abstract boolean hasCollision(BoxCollider toCheck);

    /* checks for collision with a CirlceCollider */
    public abstract boolean hasCollision(CircleCollider toCheck);

    /* Set Bounds by radius*/
    public abstract boolean setBounds(float Radius);

    /* Sets Bounds by width and height */
    public abstract boolean setBounds(float Width, float Height);

    public boolean setBounds(float Top, float Bottom, float Left, float Right){
        if (Top > Bottom && Left < Right)
        {
            top = Top;
            bottom = Bottom;
            left = Left;
            right = Right;
            return true;
        }
        return false;
    }
}

/* Performs collision detection as a rectangle, which will be used on Rectangle objects */
class BoxCollider extends Collider{
    private float width;
    private float height;

    /* checks for collision with a BoxCollider */
    public boolean hasCollision(BoxCollider toCheck){
        float centerX = transform.CenterX();
        float centerY = transform.CenterY();
        float checkX = toCheck.transform.CenterX();
        float checkY = toCheck.transform.CenterY();

        if (centerX < checkX +toCheck.width &&
                centerX + width > checkX &&
                centerY < checkY + toCheck.height &&
                centerY + height > checkY)
            return true;

        return false;
    }

    /* checks for collision with a CirlceCollider */
    public boolean hasCollision(CircleCollider toCheck){
        /* get line from the center of the circle to the center of the box */
        float dx = transform.CenterX() - toCheck.transform.CenterX();
        float dy = transform.CenterY() - toCheck.transform.CenterY();
        float mag = (float) Math.sqrt(dx * dx + dy * dy);

        /* build unit vectors */
        float unitX = dx / mag;
        float unitY = dy / mag;

        /* get furthest point on circle in unit vector direction */
        float circleX = toCheck.transform.CenterX() + unitX * toCheck.radius;
        float circleY = toCheck.transform.CenterY() + unitY * toCheck.radius;

        if (circleX > Left() && circleX < Right() &&
                circleY > Bottom() && circleY < Top())
            return true;

        return false;
    }

    /* Set Bounds by radius*/
    public boolean setBounds(float Radius){
        if (Radius < 0.0f)
            return false;

        float centerX = transform.CenterX();
        float centerY = transform.CenterY();

        width = 2 * Radius;
        height = width;
        return super.setBounds(centerY + Radius, centerY - Radius, centerX - Radius, centerX + Radius);
    }

    /* Sets Bounds by width and height */
    public boolean setBounds(float Width, float Height){
        if (Width < 0.0f || Height < 0.0f)
            return false;

        float centerX = transform.CenterX();
        float centerY = transform.CenterY();

        width = Width;
        height = Height;
        float dx = width / 2.0f;
        float dy = height / 2.0f;
        return super.setBounds(centerY + dy,  centerY - dy, centerX - dx, centerX + dx);
    }
}

/* Performs collision detection as a cirlce */
class CircleCollider extends Collider{
    float radius;

    /* checks for collision with a BoxCollider */
    public boolean hasCollision(BoxCollider toCheck){
        return toCheck.hasCollision(this);
    }

    /* checks for collision with a CirlceCollider */
    public boolean hasCollision(CircleCollider toCheck){
        if (transform.Distance(toCheck.transform) < radius + toCheck.radius)
            return true;
        return false;
    }

    /* Set Bounds by radius*/
    public boolean setBounds(float Radius){
        if (Radius < 0.0f)
            return false;

        float centerX = transform.CenterX();
        float centerY = transform.CenterY();

        radius = Radius;
        return super.setBounds(centerY + Radius, centerY - Radius, centerX - Radius, centerX + Radius);
    }

    /* Sets Bounds by width and height */
    public boolean setBounds(float Width, float Height){
        if (Width < 0.0f || Height < 0.0f)
            return false;

        float centerX = transform.CenterX();
        float centerY = transform.CenterY();

        radius = Math.min(Width, Height);
        return super.setBounds(centerY + radius,  centerY - radius, centerX - radius, centerX + radius);
    }
}

/* holds an array of colliders that it checks against.  This is currently designed to work
    for an array of BoxColliders for the Forts, but circles can be used too.
 */
class CompoundCollider extends Collider{
    private Collider[] colliders;

    public CompoundCollider(Collider[] Colliders){
        if (Colliders == null) {
            colliders = new Collider[1]; /* default to a box collider */
            colliders[0] = new BoxCollider();
        }
        else
            colliders = Colliders;
    }

    /* checks for collision with a BoxCollider and any collider in the array*/
    public boolean hasCollision(BoxCollider toCheck){
        for (int i = 0; i < colliders.length; ++i){
                if(colliders[i].hasCollision(toCheck))
                    return true;
        }
        return false;
    }

    /* checks for collision with a CirlceCollider */
    public boolean hasCollision(CircleCollider toCheck){
        for (int i = 0; i < colliders.length; ++i){
            if (colliders[i].hasCollision(toCheck))
                return true;
        }
        return false;
    }

    /* Bounds for the compound is simply the "biggest" value found in all of the colliders */
    /* Set Bounds by radius*/
    public boolean setBounds(float Radius){
        if (Radius < 0.0f)
            return false;

        for (int i = 0; i < colliders.length; ++i)
            colliders[i].setBounds(Radius);
        return setBoundsMax();
    }

    /* Sets Bounds by width and height */
    public boolean setBounds(float Width, float Height){
        if (Width < 0.0f || Height < 0.0f)
            return false;

        for (int i = 0; i < colliders.length; ++i)
            colliders[i].setBounds(Width, Height);
        return setBoundsMax();
    }

    /* performs the final bounds check to get the maximum bounds of all the colliders and set it */
    private boolean setBoundsMax(){
        float maxL = colliders[0].Left();
        float maxR = colliders[0].Right();
        float maxT = colliders[0].Top();
        float maxB = colliders[0].Bottom();

        for (int i = 1; i < colliders.length; ++i){
            if (colliders[i].Left() < maxL)
                maxL = colliders[i].Left();
            if (colliders[i].Right() > maxR)
                maxR = colliders[i].Right();
            if (colliders[i].Top() > maxT)
                maxT = colliders[i].Top();
            if (colliders[i].Bottom() < maxB)
                maxB = colliders[i].Bottom();
        }
        return super.setBounds(maxT, maxB, maxL, maxR);
    }
}