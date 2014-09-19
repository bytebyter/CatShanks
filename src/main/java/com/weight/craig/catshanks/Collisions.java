package com.weight.craig.catshanks;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by Craig on 12/10/13.
 */
public class Collisions {

    public static boolean Check(RectF a, Circle b){
        return Check(b,a);
    }
    public static boolean isPointInRect(PointF p, RectF r){
        return (p.x > r.left & p.x<r.right & p.y >r.top & p.y<r.bottom);
    }

    public static boolean isPointInRect(float x, float y, RectF r){
        return (x > r.left && x<r.right && y >r.top && y<r.bottom);
    }

    public static boolean Check(Circle a, RectF b){
        float cX=a.x();
        float cY=a.y();
        if(a.x()< b.left) cX=b.left;
        else if (a.x()>b.right) cX= b.right;

        if (a.y()<b.top) cY= b.top;
        else if (a.y()> b.bottom) cY=b.bottom;
        return ((a.x()-cX) * (a.x()-cX) + (a.y()-cY) * (a.y()-cY)) < (a.radius() * a.radius());
    }

    public static boolean Check(Circle a, Circle b){
        float r,dx, dy;
        r = (a.radius()+b.radius()) * (a.radius()+b.radius());
        dx = a.x() - b.x();
        dy = a.y() - b.y();
        return r > (dx * dx) + (dy * dy);
    }
    public static boolean Check(RectF a, RectF b){
        return !((a.bottom < b.top) | (a.top > b.bottom) |
                (a.left > b.right) | (a.right < b.left));
    }
}
