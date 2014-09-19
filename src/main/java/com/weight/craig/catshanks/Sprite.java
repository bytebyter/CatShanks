package com.weight.craig.catshanks;

import android.graphics.PointF;
import android.graphics.RectF;
import java.util.HashMap;

/**
 * Created by Craig on 11/21/13.
 */
public abstract class Sprite {
    private TextureManager TM=TextureManager.getInstance();
    private final String NO_ANIMATIONS="NO_ANIMATIONS";
    private HashMap<String,SpriteAnimation> Animations=new HashMap<String,SpriteAnimation> ();
    private String Animation=NO_ANIMATIONS;
    private float partialFrame=0;
    private int Frame=0;
    private int FPS=0;

    private int textureRef;
    public Sprite(int textureId){
        setTextureId(textureId);
    }

    public void setFPS(int fps){
        this.FPS=fps;
    }
    public RectF getFrame(){
        if(Frame>=Animations.get(Animation).getFrameCount()) Frame=0;
        return Animations.get(Animation).getFrame(Frame); }
    public ShapeF getCollisionBox(){
        if(Frame>=Animations.get(Animation).getFrameCount()) Frame=0;
        return Animations.get(Animation).getCollisionBox(Frame);
    }
    public PointF getSrcSize(){ return TM.getTextureSize(textureRef); }
    public void addAnimation(String aName,SpriteAnimation Animation){
        if (aName!=null & Animation!=null){
            if(Animation.getFrameCount()!=0){
                Animations.put(aName,Animation);
                if(this.Animation.equals(NO_ANIMATIONS)) this.Animation=aName;
            }
        }
    }

    public void removeAnimation(String Animation){
        Animations.remove(Animation);
    }

    public void FrameUpdate(float deltaTime){
        partialFrame+=(float) FPS* deltaTime;
        if(partialFrame>=1){
            partialFrame=0;
            incFrame();
        }
    }

    public void setAnimation(String Animation){
        if(Animations.containsKey(Animation)) this.Animation=Animation;
    }

    public int getTexture(){ return textureRef;}
    public void setTextureId(int textureRef){
        if (TM.containsTexture(textureRef)) this.textureRef=textureRef;
    }
    public void setTextureRes(int textureRes){
        int textureId=TM.getResourceTextureID(textureRes);
        if(textureId!=-1) textureRef= textureId ;
        else {
            textureId= TM.addTexture(textureRes);
            if (textureId!=-1) textureRef=textureId;
        }
    }

    private void incFrame(){
        if(Frame < Animations.get(Animation).getFrameCount()) Frame++;
        else Frame=0;
    }
    public void resetAnimation(){ Frame=0; }
    public abstract void Update(float deltaTime);
}
