package com.weight.craig.catshanks.BaseObjects;

import android.graphics.RectF;

/**
 * Created by Craig on 12/19/13.
 */
public abstract class Particle {
    private RectF Frame;
    private int texture;
    private float Angle=0.0f;
    private boolean Active=true;
    private float decayTime;
    private float x,y,Scale;
    private int Speed;

    public Particle(int textureId, RectF TextureCoordinates, float Scale){
        this.texture=textureId;
        Frame=TextureCoordinates;
        this.Scale=Scale;
    }

    public void setX(float x){ this.x=x; }
    public void setY(float y){ this.y=y; }
    public void setPos(float x, float y){
        this.x=x;
        this.y=y;
    }
    public float getX(){ return this.x; }
    public float getY(){ return this.y; }
    public void setSpeed(int Speed){ this.Speed=Speed; }
    public int getSpeed(){ return Speed; }
    public RectF getFrame(){ return Frame; }
    public int getTexture(){ return texture; }
    public void setAngle(float Angle){ this.Angle=Angle;}
    public float getAngle() { return this.Angle; }
    public void setDecayTime(float time){ decayTime=time;}
    public float getDecayTime(){ return decayTime; }
    public void setFrame(RectF coordinates){ Frame=coordinates; }

    public boolean isActive(){ return Active; }
    public void UpdateDecayTime(float deltatime){
        decayTime-=deltatime;
        if(decayTime<0) Active=false;
    }
    public abstract void Update(float deltaTime);


}
