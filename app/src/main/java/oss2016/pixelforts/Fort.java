package oss2016.pixelforts;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   The fort class manages its own health pool and aiming / firing weapons.
   The class also contains an array of 3 Rectangles for rendering and collision
   detection.  There is no rendering or collision detection on the Fort itself.
*/
public class Fort extends Transform{
    private int health = 100;

    private float width;
    private float height;
    private Collider collider;
    private Rectangle[] rectangles;
    private boolean needsRedrawn;

    public Fort(){
        super();
        setupFort(1.0f, 1.0f);
    }

    public Fort(float CenterX, float CenterY, float Width, float Height){
        super(CenterX, CenterY);
        setupFort(Width, Height);
    }

    /* create the rectangles if not done yet and position them and set their dimensions
    * The rectangles are [0] main keep, [1] right tower, [2] left tower*/
    private void setupFort(float Width, float Height){
        if (Width < 0 || Height < 0)
            return;

        if (rectangles == null) {
            rectangles = new Rectangle[3];
            for (int i = 0; i < rectangles.length; ++i) {
                rectangles[i] = new Rectangle();
                rectangles[i].addRenderer();
                rectangles[i].setColor(1.0f, .2f, .2f, 0.0f);
            }
            Collider temp = new BoxCollider(); /* compound collider for Fort */
            setCollider(temp);
        }

        width = Width;
        height = Height;

        /* calculate the new offsets for the rectangles */
        rectangles[0].SetCenter(CenterX(), CenterY() - .1f * height);
        rectangles[1].SetCenter(CenterX() - .35f * width, CenterY() + .4f * height);
        rectangles[2].SetCenter(CenterX() + .35f * width, CenterY() + .4f * height);

        /* set the appropriate dimensions, which recalculates the vertices and collider */
        rectangles[0].setDimensions(Width, .8f * Height);
        rectangles[1].setDimensions(0.3f * Width, .2f * Height);
        rectangles[2].setDimensions(0.3f * Width, .2f * Height);
    }

    /* adds the renderer needed for each rectangle */
    public int addRenderer(){
        int result = 1;
        for (int i = 0; i < rectangles.length; ++i) {
            if (rectangles[i].addRenderer() == 0)
                result = 0;
        }
        return result;
    }

    /* removes the renderer for each rectangle */
    public int removeRenderer() {
        int result = 1;
        for (int i = 0; i < rectangles.length; ++i) {
            if (rectangles[i].removeRenderer() == 0)
                result = 0;
        }
        return result;
    }

    /* set the color of the renderers */
    public void setColor(float red, float green, float blue, float alpha){
        for(int i = 0; i < rectangles.length; ++i){
            rectangles[i].setColor(red, green, blue, alpha);
        }
    }

    /* adds a collider to the object for collision detection
    * (Adding a collider other than the compound one is not recommended ) */
    public void setCollider(Collider toSet){
        super.setCollider(toSet);
        collider = toSet;
    }

    /* Sets the width and height the entire Fort is supposed to have */
    public boolean setDimensions(float Width, float Height){
        if (Height > 0.0f && Width > 0.0f) {
            width = Width;
            height = Height;

            setupFort(Width, Height);
            if (collider != null)
                collider.setBounds(Width, Height);
            return true;
        }
        return false;
    }

    /* Build the vertices for each rectangle so they can be drawn */
    public void buildVertices(){
        for (int i = 0; i < rectangles.length; ++i) {
            rectangles[i].buildVertices();
        }
    }

    /* Draw the rectangles that make up the keep */
    public void Draw(float[] mvpMatrix){
        for (int i = 0; i < rectangles.length; ++i) {
            rectangles[i].Draw(mvpMatrix);
        }
    }

    /* called by projectiles on collision */
    public void dealDamage(int damage){
        health -= damage;
        if (health <= 0)
            setDead(true);
        setDimensions(0.0f, 0.0f);
    }

    /* Returns the bounds of the object */
    public float Top(){
        if (collider != null)
            return collider.Top();
        return CenterY() + height / 2.0f;
    }
    public float Bottom(){
        if (collider != null)
            return collider.Bottom();
        return CenterY() - height / 2.0f;
    }
    public float Left(){
        if (collider != null)
            return collider.Left();
        return CenterX() - width / 2.0f;
    }
    public float Right(){
        if (collider != null)
            return collider.Right();
        return CenterY() + width / 2.0f;
    }
}
