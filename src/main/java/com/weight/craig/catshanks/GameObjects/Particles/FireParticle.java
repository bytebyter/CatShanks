package com.weight.craig.catshanks.GameObjects.Particles;

import android.graphics.RectF;
import android.util.FloatMath;

import com.weight.craig.catshanks.BaseObjects.Particle;
import com.weight.craig.catshanks.R;
import com.weight.craig.catshanks.Support.Resolution;
import com.weight.craig.catshanks.Managers.TextureManager;

import java.util.Random;

/**
 * Created by Craig on 12/21/13.
 */
public class FireParticle extends Particle {
    Random rnd=new Random();

    public FireParticle(float x, float y, float angle, int Speed) {
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2), new RectF(896,704,960,768), Resolution.getInstance().getScale());
        if(rnd.nextBoolean()) setFrame(new RectF(960,640,1024,704));

        setAngle(angle);
        setPos(x,y);
        setSpeed(Speed);
    }

    public void Update(float deltaTime) {
        if(isActive()){
            setPos(getX()+(FloatMath.cos(getAngle()) * (float)getSpeed()), getY() - (FloatMath.sin(getAngle()) * getSpeed()));
            UpdateDecayTime(deltaTime);
        }
    }
}
