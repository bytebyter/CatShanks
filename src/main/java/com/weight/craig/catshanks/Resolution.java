package com.weight.craig.catshanks;

import android.graphics.PointF;

/**
 * Created by Craig on 12/15/13.
 */
public class Resolution {
    private PointF resolution=new PointF();
    private float Scale=1;

    private static Resolution instance;
    public static Resolution getInstance(){
        if (instance==null){
            instance= new Resolution();
        }
        return instance;
    }
    private Resolution(){}
    public void setResolution(PointF resolution){this.resolution=resolution;}
    public PointF getResolution(){
        return resolution;
    }
    public void setScale(float scale){
        Scale=scale;
    }
    public float getScale(){ return Scale; }
    public float getWidth(){
        if(resolution!=null)return resolution.x;
        return 0;
    }
    public float getHeight(){
        if(resolution!=null)return resolution.y;
        return 0;
    }
    public float PercentageOfWidth(float size){
        if(resolution!=null)return resolution.x/size;
        return 0;
    }
    public float PercentageOfHeight(float size){
        if(resolution!=null)return resolution.y/size;
        return 0;
    }
}
