package oss2016.pixelforts;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

    The Player base class contains an object that they are playing with and their name.
*/
public abstract class Player {
    private String name;
    private Fort fort;
    private Weapon weapon;

    public String Name() { return name;}
    public Fort Fort() { return fort;}
    public Weapon currentWeapon() { return weapon;}
    public boolean IsDead() {
        if (fort != null)
            return fort.IsDead();
        return true;
    }

    public Player(){
        name = null;
        fort = null;
        weapon = null;
    }

    public Player(String Name){
        name = Name;
        fort = null;
    }

    public void buildFort(){
        fort = new Fort();
    }

    /* initialize the player's fort */
    public void buildFort(float CenterX, float CenterY, float Width, float Height){
        fort = new Fort(CenterX, CenterY, Width, Height);
        fort.getCollider().setBounds(Width, Height);
    }

    public void setWeapon(Weapon active){
        weapon = active;
    }

    public void destroyWeapon(){
        weapon.destroy();
        weapon = null;
    }
}
