package oss2016.pixelforts;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   The weapon class fires projectiles with a specific velocity and direction.
*/
public class Weapon {
    private float unitX; /* unit vector direction */
    private float unitY;
    private Projectile projectile;

    public float UnitX(){ return unitX;}
    public float UnitY(){ return unitY;}

    public Weapon(Projectile nProjectile){
        projectile = nProjectile;
        unitY = 0;
        unitX = 1;
    }

    /* calculates directionX and Y*/
    public void aim(float angle){

    }

    public Projectile fire(float magnitude){
        return projectile;
    }

    public void Update(){

    }


}
