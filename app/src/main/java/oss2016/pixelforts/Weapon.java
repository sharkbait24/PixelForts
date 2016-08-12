package oss2016.pixelforts;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   The weapon class fires projectiles with a specific velocity and direction.
*/
public class Weapon {
    private float unitX; /* unit vector direction */
    private float unitY;
    private float centerX; /* position */
    private float centerY;
    private int damage;

    public float UnitX(){ return unitX;}
    public float UnitY(){ return unitY;}

    public Weapon(float CenterX, float CenterY, int Damage){
        damage = Damage;
        centerX = CenterX;
        centerY = CenterY;
        unitY = 1;
        unitX = 0;
    }

    /* calculates directionX and Y*/
    public void aim(float angle){

    }

    /* Create a new projectile in the direction aiming and give it a velocity */
    public Projectile fire(float magnitude){
        Projectile bullet = new Projectile(centerX, centerY, .05f, .05f, damage);
        bullet.ApplyForce(unitX * magnitude, unitY * magnitude);
        return bullet;
    }
}
