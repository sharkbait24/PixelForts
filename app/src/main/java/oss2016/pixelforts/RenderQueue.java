package oss2016.pixelforts;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   Handles the storing of references to objects that need to be redrawn.
   After drawing each object we check if the object still needs to be drawn again next frame.
   If it doesn't, the object is removed from the render queue.
 */
public class RenderQueue {
    private Node head;
    private Node tail;
    private Node free; /* free RenderNode list */
    private float[] mMVPMatrix;

    public RenderQueue(float[] MMVPMatrix){
        mMVPMatrix = MMVPMatrix;
    }

    /* Add a new node at the tail */
    public void Add(Transform toAdd){
        if (toAdd == null)
            return;

        Node temp;
        if (free != null) {
            temp = nextFree();
            temp.object = toAdd;
        }
        else
            temp = new Node(toAdd);

        if (tail == null) {
            head = temp;
            tail = temp;
        }
        else {
            tail.next = temp;
            tail = temp;
        }
    }

    public boolean Remove(Transform toRemove){
        if (head == null)
            return false;

        Node current = head;
        Node previous = null;
        while (current != null){
            if (current.object == toRemove){
                Node temp = current;
                current = current.next;
                if (previous == null)
                    head = current;
                else
                    previous.next = current;

                addFree(temp);
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    /* calls draw on every object in the queue and after checks if it still needs to be redrawn.
        If needsRedrawn returns false, then the object is removed from the queue.
     */
    public void DrawAll(){
        if (head == null)
            return;

        Node current = head;
        Node previous = null;
        while (current != null){
            if (current.object != null) {
                current.object.Draw(mMVPMatrix);
                if (current.object.NeedsRedrawn()){
                    previous = current;
                    current = current.next;
                    continue;
                }

            }
            /* object needs to be removed */
            Node toRemove = current;
            current = current.next;
            if (previous == null)
                head = current;
            else
                previous.next = current;

            addFree(toRemove);
        }
    }

    /* The free list will hold all of the RenderNodes that were instantiated but not
    currently used.  This will help by limiting the amount of heap allocation calls. */
    private void addFree(Node toAdd){
        toAdd.next = free;
        free = toAdd;
        toAdd.object = null;
    }

    /* Remove the first node in the list */
    private Node nextFree(){
        if (free == null)
            return null;

        Node temp = free;
        free = free.next;
        return  temp;
    }

    /* All the GameManager to empty this list, especially after the game load when hundreds
        of objects will be in the queue. */
    public void emptyFreeList(){
        free = null;
    }

    /* Destroy the entire queue */
    public void removeAll(){
        head = null;
        tail = null;
        free = null;
    }
}

/* Simple struct object to hold the references */
class Node {
    Transform object;
    Node next;

    public Node(Transform toAdd){
        object = toAdd;
        next = null;
    }
}