package com.weight.craig.catshanks.BaseObjects;

import android.util.SparseArray;

/**
 * Created by Craig on 12/18/13.
 */
public abstract class PowerUp extends NPC {

    private float timer;

    public PowerUp(int textureId, SparseArray<SpriteAnimation> animationSet, float Scale) {
        super(textureId);
        for(int i=0; i<animationSet.size();i++){
            this.addAnimation(i,animationSet.get(animationSet.keyAt(i)));
        }
        this.setAnimation_Looping(0);
        setScale(Scale);
    }
    public void setTimer(float time){ timer=time;}
    public float getDecayTime(){ return timer; }

    private void updateTimer(float time){
        timer-=time;
        if(timer<=0) setActive(false);
    }


    @Override
    public void Update(float deltaTime) {
        if(isActive()) updateTimer(deltaTime);
    }
}
