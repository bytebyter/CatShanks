package com.weight.craig.catshanks;

import android.graphics.RectF;

/**
 * Created by Craig on 12/10/13.
 */
public class ShapeF {
    private Circle c=null;
    private RectF r=null;
    public ShapeF(RectF r){
        this.r=r;
    }
    public ShapeF(Circle c){
        this.c=c;
    }
    public boolean isRect(){ return r!=null; }
    public boolean isCircle() {return c!=null;}
    public Circle getCircle(){ return c;}
    public RectF getRect(){ return r;}

}
