package oss2016.pixelforts;

import java.util.concurrent.locks.ReentrantLock;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information.

   Handles the storing of references to objects that need to be drawn.  Since the Render is set
   to

   Also added a lock to all public functions since this may be accessed by multiple threads in
   the future.
 */
public class RenderQueue {
    private Node head;
    private Node tail;
    private float[] mMVPMatrix;

    private final ReentrantLock lock = new ReentrantLock();

    public RenderQueue(float[] MMVPMatrix){
        mMVPMatrix = MMVPMatrix;
    }

    /* Add a new node at the tail that references the Transform
    * DrawOnce will remove the node after the first Draw call*/
    public void Add(Transform toAdd){
        lock.lock();
        try {
            if (toAdd == null) {
                return;
            }

            Node temp = new Node(toAdd);

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

    /* Remove a specific node, when it no longer needs to be drawn */
    public boolean remove(Transform toRemove){
        lock.lock();
        try {
            if (head == null) {
                return false;
            }

            Node current = head;
            Node previous = null;
            while (current != null) {
                if (current.object == toRemove) {
                    current = current.next;
                    if (current == null)
                        tail = previous;
                    if (previous == null) {
                        head = current;
                    } else
                        previous.next = current;

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

    /* calls draw on every object in the queue.  If the node is marked drawOnce, the node is removed
     */
    public void DrawAll(){
        lock.lock();
        try {
            if (head == null) {
                return;
            }

            Node current = head;
            Node previous = null;
            while (current != null) {
                if (current.object != null) {
                    current.object.Draw(mMVPMatrix);
                    current = current.next;
                    continue;
                }
            /* null object needs to be removed */
                Node toRemove = current;
                current = current.next;
                if (toRemove.next == null)
                    tail = previous;
                if (previous == null)
                    head = current;
                else
                    previous.next = current;
            }
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