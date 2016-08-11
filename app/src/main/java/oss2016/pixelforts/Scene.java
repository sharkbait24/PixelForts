package oss2016.pixelforts;

import java.util.Random;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   The Scene class keeps track of the position of every object on the screen, and determines
   which objects should be check for collisions.  This class also manages the generation of the land.
*/
public class Scene {
    private Land[] land;
    private Region[] regions;
    private RenderQueue renderQueue;

    public Scene (Fort[] players){
        renderQueue = GMGLRenderer.getRenderQueue();
        generateLand();
    }

    /* Some random weights applied to a sin curve to make the "rolling hills" style */
    private void generateLand(){
        Random rand = new Random();
        float x = -2.0f;
        float random = (float)(Math.abs(rand.nextInt()) % 150) / 100.0f + 1.0f;
        float modX = x;
        float height;
        land = new Land[85];
        for (int i = 0; i < land.length; ++i){
            land[i] = new Land();
            land[i].addRenderer();
            if (i % 10 == 0) {
                random = (float) (Math.abs(rand.nextInt()) % 150) / 100.0f + 1.0f;
            }
            height = 0.75f + (float) (Math.sin(2.0f * modX) / 1.5f);
            land[i].SetCenter(x, -1.0f + height / 2.0f);
            land[i].setDimensions(0.05f, height);
            land[i].setCollider(new BoxCollider());
            x += 0.05f;
            modX = modX + 0.05f * random;
            renderQueue.Add(land[i]);
        }
    }
}

/* holds the start of the list and the horizontal coordinates the the region spans
* The list is sorted in descending order, which should ease collision checking */
class Region{
    float left;
    float right;
    RegionNode head;

    public void setBounds(float Left, float Right){
        if (Left < Right) {
            left = Left;
            right = Right;
        }
    }

    /* Add in descending order */
    public void add(Transform toAdd){
        if (toAdd == null)
            return;

        RegionNode current = head;
        RegionNode previous = null;
        RegionNode temp = new RegionNode(toAdd);
        while (current != null && current.object.Top() > toAdd.Top()) {
            previous = current;
            current = current.next;
        }
        if (previous == null) {
            temp.next = head;
            head = temp;
        }
        else {
            temp.next = current;
            previous.next = temp;
        }
    }

    /* empty the list */
    public void removeAll(){
        head = null;
    }
}

class RegionNode{
    Transform object;
    RegionNode next;

    public RegionNode(Transform toAdd){
        object = toAdd;
        next = null;
    }
}
