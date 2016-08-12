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

    private float maxX = 1.0f; /* angle limits for weapon */
    private float minX = -1.0f;
    private float maxY = 1.0f;
    private float minY = 0.0f;

    private float charge;
    private float maxCharge;
    private int damage;
    private Crosshair crosshair;

    public float UnitX(){ return unitX;}
    public float UnitY(){ return unitY;}
    public float CenterX(){ return centerX; }
    public float CenterY(){ return centerY; }

    public Weapon(float CenterX, float CenterY, int Damage, float MaxCharge){
        damage = Damage;
        maxCharge = MaxCharge;
        centerX = CenterX;
        centerY = CenterY;
        unitY = 1;
        unitX = 0;
        charge = 0.0f;
        crosshair = new Crosshair(this);
    }

    /* calculates unitX and Y within the given limits*/
    public void aim(float X, float Y){
        /* normalize */
        float dx = X - centerX;
        float dy = Y- centerY;
        float mag = (float) Math.sqrt(dx * dx + dy * dy);

        float xNorm = dx / mag;
        float yNorm = dy / mag;

        if (xNorm > maxX)
            unitX = maxX;
        else if (xNorm < minX)
            unitX = minX;
        else
            unitX = xNorm;

        if (yNorm > maxY)
            unitY = maxY;
        else if (yNorm < minY)
            unitY = minY;
        else
            unitY = yNorm;
        
        crosshair.update(centerX, centerY, unitX, unitY);
    }

    /* Add onto charge value */
    public void charge(float toAddPercent){
        charge += toAddPercent * maxCharge;
        if (charge > maxCharge)
            charge = maxCharge;

        crosshair.showCharge(charge / maxCharge);
    }

    /* Create a new projectile in the direction aiming and give it a velocity */
    public Projectile fire(){
        Projectile bullet = new Projectile(centerX + .2f * unitX, centerY + .2f * unitY, .05f, .05f, damage);
        bullet.ApplyForce(unitX * charge, unitY * charge);
        return bullet;
    }

    /* removes the crosshair */
    public void destroy(){
        crosshair.destroy();
        crosshair = null;
    }
}

/* Used by the weapon class to show on screen where the weapon is aiming */
class Crosshair{
    Weapon weapon;
    Rectangle centerDot;

    /* the outer lines will change size to show the user how much charge they have generated */
    Rectangle[] outerLines = new Rectangle[4];
    private float defaultSize = 0.1f;
    private float small = defaultSize / 5.0f;

    public Crosshair(Weapon thisWeapon){
        weapon = thisWeapon;
        RenderQueue renderQueue = GMGLRenderer.getRenderQueue();

        centerDot = new Rectangle(weapon.CenterX(), weapon.CenterY(), small, small);
        centerDot.addRenderer();
        centerDot.setColor(1.0f, 1.0f, 1.0f, 0.0f);
        renderQueue.Add(centerDot);

        outerLines[0] = new Rectangle(weapon.CenterX(), weapon.CenterY(), defaultSize, small);
        outerLines[1] = new Rectangle(weapon.CenterX(), weapon.CenterY(), small, defaultSize);
        outerLines[2] = new Rectangle(weapon.CenterX(), weapon.CenterY(), defaultSize, small);
        outerLines[3] = new Rectangle(weapon.CenterX(), weapon.CenterY(), small, defaultSize);
        for (int i = 0; i < outerLines.length; ++i){
            outerLines[i].addRenderer();
            outerLines[i].setColor(1.0f, 1.0f, 1.0f, 0.0f);
            renderQueue.Add(outerLines[i]);
        }

        update(weapon.CenterX(), weapon.CenterY(), weapon.UnitX(), weapon.UnitY());
        showCharge(0.0f);
    }

    public void update(float centerX, float centerY, float unitX, float unitY) {
        centerDot.SetCenter(centerX + .4f * unitX, centerY + .4f * unitY);
        centerDot.Update();
        outerLines[0].SetCenter(centerX + .4f * unitX - .2f, centerY + .4f * unitY);
        outerLines[0].Update();
        outerLines[1].SetCenter(centerX + .4f * unitX, centerY + .4f * unitY + .2f);
        outerLines[1].Update();
        outerLines[2].SetCenter(centerX + .4f * unitX + .2f, centerY + .4f * unitY);
        outerLines[2].Update();
        outerLines[3].SetCenter(centerX + .4f * unitX, centerY + .4f * unitY - .2f);
        outerLines[3].Update();
    }

    /* update the outerlines to reflect the percent charged */
    public void showCharge(float percent){
        outerLines[0].setDimensions(defaultSize + percent * defaultSize, small);
        outerLines[1].setDimensions(small, defaultSize + percent * defaultSize);
        outerLines[2].setDimensions(defaultSize + percent * defaultSize, small);
        outerLines[3].setDimensions(small, defaultSize + percent * defaultSize);
    }

    /* remove all of the rectangles from the renderQueue */
    public void destroy(){
        RenderQueue renderQueue = GMGLRenderer.getRenderQueue();

        renderQueue.remove(centerDot);
        for (int i = 0; i < outerLines.length; ++i)
            renderQueue.remove(outerLines[i]);
        outerLines = null;
        centerDot = null;
    }
}
