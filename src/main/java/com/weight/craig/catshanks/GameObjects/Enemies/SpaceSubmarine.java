package com.weight.craig.catshanks.GameObjects.Enemies;

import java.util.Random;
import android.util.FloatMath;
import com.weight.craig.catshanks.R;
import com.weight.craig.catshanks.BaseObjects.Enemy;
import com.weight.craig.catshanks.Support.MathF;
import com.weight.craig.catshanks.Support.Resolution;
import com.weight.craig.catshanks.Managers.TextureManager;
import com.weight.craig.catshanks.Managers.AnimationManager;

/**
 * Created by Craig on 12/16/13.
 */
public class SpaceSubmarine extends Enemy{
    Random rnd=new Random();
    private static final String Anim="Space-Submarine";

    public SpaceSubmarine(float x, int Speed) {
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2),
                AnimationManager.getInstance().getAnimationSet(Anim), Resolution.getInstance().getScale());
        addWeapon(com.weight.craig.catshanks.Weapon.CannonBall);
        setWeapon(com.weight.craig.catshanks.Weapon.CannonBall);
        setAutoAim(true);
        setFireRate(2f);

        setScoreValue(200);
        setHp(40);
        setSpeed(Speed);
        setDestroyOnCollide(false);
        init(x);
        setActive(true);
    }

    /**
     * Used for loading purposes.
     * @param x
     * @param y
     * @param angle
     * @param Speed
     * @param timeToFire
     */
    public SpaceSubmarine(float x, float y, float angle, int Speed, int Hp, float timeToFire) {
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2),
                AnimationManager.getInstance().getAnimationSet(Anim), Resolution.getInstance().getScale());
        addWeapon(com.weight.craig.catshanks.Weapon.CannonBall);
        setWeapon(com.weight.craig.catshanks.Weapon.CannonBall);
        setAutoAim(true);
        setFireRate(2f);
        setScoreValue(200);
        setHp(Hp);
        setSpeed(Speed);
        setTimetoFire(timeToFire);
        setDestroyOnCollide(false);
        setPos(x,y);
        setAngle(angle);
        setActive(true);
    }

    private void init(float x){
        switch(rnd.nextInt(2)){
            case(0):            //Top
                setPos(x,0);
                setAngle(MathF.PI + (rnd.nextFloat()*MathF.PIOver16));
                break;
            case(1):            //Bottom
                setPos(x,Resolution.getInstance().getHeight()-(getFrame().height()*getScale()));
                setAngle(MathF.PI - (rnd.nextFloat()*MathF.PIOver16));
                break;
        }
    }

    @Override
    public void AI(float deltaTime) {
        decTimeToFire(deltaTime);
        //Move Left
        setPos(getX()+(FloatMath.cos(getAngle()) * (float)getSpeed()), getY() - (FloatMath.sin(getAngle()) * getSpeed()));
        if(getY()>Resolution.getInstance().getHeight()) setActive(false);
        else if (getY()+(getFrame().height() * getScale())<0) setActive(false);
        else if (getX()+(getFrame().width()*getScale())<0) setActive(false);

    }

    @Override
    public void Update(float deltaTime) {
        if(isActive()) AI(deltaTime);
    }
}
