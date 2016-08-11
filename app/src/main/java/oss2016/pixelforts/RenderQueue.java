package oss2016.pixelforts;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   Handles the storing of references to objects that need to be redrawn.
   After drawing each object we check if the object still needs to be drawn again next frame.
   If it doesn't, the object is removed from the render queue.
 */
public class RenderQueue {
    private RenderNode head;
    private RenderNode tail;
    private RenderNode free; /* free RenderNode list */
    private float[] mMVPMatrix;

    public RenderQueue(float[] MMVPMatrix){
        mMVPMatrix = MMVPMatrix;
    }

    /* Add a new node at the tail */
    public void Add(Transform toAdd){
        if (toAdd == null)
            return;

        RenderNode temp;
        if (free != null) {
            temp = nextFree();
            temp.Object = toAdd;
        }
        else
            temp = new RenderNode(toAdd);

        if (tail == null)
            head = tail = temp;
        else
            tail.Next = temp;
    }

    public boolean Remove(Transform toRemove){
        if (head == null)
            return false;

        RenderNode current = head;
        RenderNode previous = null;
        while (current != null){
            if (current.Object == toRemove){
                RenderNode temp = current;
                current = current.Next;
                if (previous == null)
                    head = current;
                else
                    previous.Next = current;

                putFree(temp);
                return true;
            }
            previous = current;
            current = current.Next;
        }
        return false;
    }

    /* calls draw on every object in the queue and after checks if it still needs to be redrawn.
        If needsRedrawn returns false, then the object is removed from the queue.
     */
    public void DrawAll(){
        if (head == null)
            return;

        RenderNode current = head;
        RenderNode previous = null;
        while (current != null){
            if (current.Object != null) {
                current.Object.Draw(mMVPMatrix);
                if (current.Object.NeedsRedrawn()){
                    previous = current;
                    current = current.Next;
                    continue;
                }

            }
            /* object needs to be removed */
            RenderNode toRemove = current;
            current = current.Next;
            if (previous == null)
                head = current;
            else
                previous.Next = current;

            putFree(toRemove);
        }
    }

    /* The free list will hold all of the RenderNodes that were instantiated but not
    currently used.  This will help by limiting the amount of heap allocation calls. */
    private void putFree(RenderNode toAdd){
        toAdd.Next = free;
        free = toAdd;
    }

    /* Remove the first node in the list */
    private RenderNode nextFree(){
        if (free == null)
            return null;

        RenderNode temp = free;
        free = free.Next;
        return  temp;
    }
}

/* Simple struct object to hold the references */
class RenderNode {
    public Transform Object;
    public RenderNode Next;

    public RenderNode(Transform toAdd){
        Object = toAdd;
        Next = null;
    }
}