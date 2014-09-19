package com.weight.craig.catshanks.GameObjects.Particles;

import android.graphics.RectF;
import android.util.FloatMath;

import com.weight.craig.catshanks.BaseObjects.Particle;
import com.weight.craig.catshanks.R;
import com.weight.craig.catshanks.Support.Resolution;
import com.weight.craig.catshanks.Managers.TextureManager;

/**
 * Created by Craig on 12/19/13.
 */
public class CollisionParticle extends Particle  {
    public CollisionParticle(float x, float y, float angle, int Speed) {
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2), new RectF(896,640,960,704), Resolution.getInstance().getScale());
        setAngle(angle);
        setPos(x,y);
        setSpeed(Speed);
        setDecayTime(2);
    }

    /**
     * Used for loading...
     * @param x
     * @param y
     * @param angle
     * @param Speed
     * @param decayTime
     */
    public CollisionParticle(float x, float y, float angle, int Speed, float decayTime) {
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2), new RectF(896,640,960,704), Resolution.getInstance().getScale());
        setAngle(angle);
        setPos(x,y);
        setSpeed(Speed);
        setDecayTime(decayTime);
    }

    public void Update(float deltaTime) {
        if(isActive()){
            setPos(getX()+(FloatMath.cos(getAngle()) * (float)getSpeed()), getY() - (FloatMath.sin(getAngle()) * getSpeed()));
            UpdateDecayTime(deltaTime);
        }
    }

}
