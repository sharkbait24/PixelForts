package oss2016.pixelforts;

/* Copyright (c) 2016 Joe Coleman
   This program is available under the "MIT" license.
   Please see the COPYING file for license information..
 */
public class Square extends Shape {

    /* checks for collision with a Circle object */
    public boolean hasCollision(Circle circle){
        return false;
    }

    /* checks for collision with a Square object */
    public boolean hasCollision(Square square){
        return false;
    }
}
