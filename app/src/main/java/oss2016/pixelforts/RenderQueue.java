package oss2016.pixelforts;

import java.util.concurrent.locks.ReentrantLock;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   Handles the storing of references to objects that need to be redrawn.
   After drawing each object we check if the object still needs to be drawn again next frame.
   If it doesn't, the object is removed from the render queue.

   Also added a lock to all public functions since this may be accessed by multiple threads in
   the future.
 */
public class RenderQueue {
    private Node head;
    private Node tail;
    private Node free; /* free RenderNode list */
    private float[] mMVPMatrix;

    private final ReentrantLock lock = new ReentrantLock();

    public RenderQueue(float[] MMVPMatrix){
        mMVPMatrix = MMVPMatrix;
    }

    /* Add a new node at the tail */
    public void Add(Transform toAdd){
        lock.lock();
        try {
            if (toAdd == null) {
                lock.unlock();
                return;
            }

            Node temp;
            if (free != null) {
                temp = nextFree();
                temp.object = toAdd;
            } else
                temp = new Node(toAdd);

            if (tail == null) {
                head = temp;
                tail = temp;
            } else {
                tail.next = temp;
                tail = temp;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(Transform toRemove){
        lock.lock();
        try {
            if (head == null) {
                lock.unlock();
                return false;
            }

            Node current = head;
            Node previous = null;
            while (current != null) {
                if (current.object == toRemove) {
                    Node temp = current;
                    current = current.next;
                    if (temp.next == null)
                        tail = previous;
                    if (previous == null) {
                        head = current;
                    } else
                        previous.next = current;

                    addFree(temp);
                    lock.unlock();
                    return true;
                }
                else {
                    previous = current;
                    current = current.next;
                }
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    /* calls draw on every object in the queue and after checks if it still needs to be redrawn.
        If needsRedrawn returns false, then the object is removed from the queue.
     */
    public void DrawAll(){
        lock.lock();
        try {
            if (head == null) {
                lock.unlock();
                return;
            }

            Node current = head;
            Node previous = null;
            while (current != null) {
                if (current.object != null) {
                    current.object.Draw(mMVPMatrix);
                    if (current.object.NeedsRedrawn()) {
                        previous = current;
                        current = current.next;
                        continue;
                    }

                }
            /* object needs to be removed */
                Node toRemove = current;
                current = current.next;
                if (toRemove.next == null)
                    tail = previous;
                if (previous == null)
                    head = current;
                else
                    previous.next = current;

                addFree(toRemove);
            }
        } finally {
            lock.unlock();
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
        lock.lock();
        try {
            free = null;
        } finally {
            lock.unlock();
        }

    }

    /* Destroy the entire queue */
    public void removeAll(){
        lock.lock();
        try {
            head = null;
            tail = null;
            free = null;
        }finally {
            lock.unlock();
        }
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