package com.weight.craig.catshanks;

/**
 * Created by Craig on 12/10/13.
 */
public class Circle {
    private float radius;
    private float centerX,centerY;

    public Circle(float x, float y,float radius){
        this.centerX=x;
        this.centerY=y;
        this.radius=radius;
    }
    public float x(){ return centerX;}
    public float y() { return centerY;}
    public float radius(){ return radius; }
    public void setAttribs(float x, float y,float radius){
        centerX=x;
        centerY=y;
        this.radius=radius;
    }
    public void setPos(float x, float y){
        this.centerX=x;
        this.centerY=y;
    }
    public void setX(float x){ centerX=x; }
    public void setY(float y){ centerY=y; }

}
