package oss2016.pixelforts;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   The projectile class handles moving objects that do damage on collision.
   This class currently derives from Rectangle because its the only class fully implemented,
   but also if I ever get textures working, Rectangle would actually be the best choice.

   Also for now physics will be managed in the Transform class until a proper class is created.
*/
public class Projectile extends Rectangle {
    private int damage;
    private Collider collider;

    public int getDamage() { return damage;}

    public Projectile(float CenterX, float CenterY, float Width, float Height){
        super(CenterX, CenterY, Width, Height);
        addRenderer();
        collider = new CircleCollider();
        super.setCollider(collider);
        super.setDimensions(Width, Height);
    }

    /* lazy way out but the project should be continually drawn until it dies */
    @Override
    public boolean NeedsRedrawn() { return true; }

    public boolean hasCollision(Transform toCheck) {
        if (super.hasCollision(toCheck)) {
            dealDamage(damage);
            setDead(true);
        }
        return false;
    }
}
