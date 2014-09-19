package com.weight.craig.catshanks.BaseObjects;

import android.graphics.RectF;
import com.weight.craig.catshanks.Collision.ShapeF;
import java.util.ArrayList;

/**
 * Created by Craig on 11/22/13.
 */
public class SpriteAnimation {
    public static final int DefaultAnim=0;

    private ArrayList<RectF> Frames= new ArrayList<RectF>();
    private ArrayList<ShapeF> ColBoxes=new ArrayList<ShapeF>();

    public SpriteAnimation(){}
    public void addFrame(int x, int y, int width,int height,ShapeF CollisionBox){
        Frames.add(new RectF(x,y,width,height));
        ColBoxes.add(CollisionBox);
    }


    public ShapeF getCollisionBox(int Frame){ return ColBoxes.get(Frame);}
    public int getFrameCount(){ return Frames.size(); }
    public RectF getFrame(int Frame){
        if (Frame<Frames.size()) return Frames.get(Frame);
        return null;
    }

}
