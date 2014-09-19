package com.weight.craig.catshanks;

import java.util.HashMap;

/**
 * Created by Craig on 12/18/13.
 */
public abstract class PowerUp extends NPC {
    private boolean Active=true;
    private float timer;

    public PowerUp(int textureId,HashMap<String,SpriteAnimation> animationSet,float Scale) {
        super(textureId);
        for(String animation:animationSet.keySet()){
            addAnimation(animation,animationSet.get(animation));
        }
        setAnimation("Default");
        setScale(Scale);
    }
    public void setTimer(float time){ timer=time;}
    public float getDecayTime(){ return timer; }

    private void updateTimer(float time){
        timer-=time;
        if(timer<=0) Active=false;
    }
    public boolean isActive(){ return Active; }
    public void setActive(boolean active){ this.Active=active; }

    @Override
    public void Update(float deltaTime) {
        if(isActive()) updateTimer(deltaTime);
    }
}
