package com.weight.craig.catshanks.GameObjects.Enemies;

import java.util.Random;
import android.util.FloatMath;
import com.weight.craig.catshanks.R;
import com.weight.craig.catshanks.BaseObjects.Enemy;
import com.weight.craig.catshanks.GameObjects.Players.Player;
import com.weight.craig.catshanks.Support.MathF;
import com.weight.craig.catshanks.Support.Resolution;
import com.weight.craig.catshanks.Managers.TextureManager;
import com.weight.craig.catshanks.Managers.AnimationManager;


/**
 * Created by Craig on 12/16/13.
 */
public class SpaceCrab extends Enemy {
    private float Bearing;
    private static final String Anim="Space-Crab";
    private Player player;
    private Random rnd=new Random();

    public SpaceCrab(float x, float y, int Speed, Player player){
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2),
                AnimationManager.getInstance().getAnimationSet(Anim), Resolution.getInstance().getScale());
        this.player=player;
        addWeapon(com.weight.craig.catshanks.Weapon.Claw);
        setWeapon(com.weight.craig.catshanks.Weapon.Claw);
        setFireRange(MathF.PI - MathF.PIOver8,MathF.PI + MathF.PIOver8);
        setFireRate(2);

        setHp(40);
        setScoreValue(300);
        setDestroyOnCollide(false);
        setSpeed(Speed);
        setActive(true);
        setPos(x,y);
        setAngle(MathF.PI);
        setNextUpdate(rnd.nextFloat()*2);
    }

    /**
     * Used for Loading.
     * @param x
     * @param y
     * @param Speed
     * @param angle
     * @param Hp
     * @param timeToFire
     */
    public SpaceCrab(float x, float y, int Speed, float angle, int Hp, float timeToFire){
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2),
                AnimationManager.getInstance().getAnimationSet(Anim), Resolution.getInstance().getScale());
        addWeapon(com.weight.craig.catshanks.Weapon.Claw);
        setWeapon(com.weight.craig.catshanks.Weapon.Claw);
        setFireRange(MathF.PI - MathF.PIOver8,MathF.PI + MathF.PIOver8);
        setFireRate(2);

        setHp(Hp);
        setTimetoFire(timeToFire);
        setScoreValue(300);
        setDestroyOnCollide(false);
        setSpeed(Speed);
        setActive(true);
        setPos(x,y);
        setAngle(angle);
        setNextUpdate(rnd.nextFloat()*2);
    }

    public void init_onLoad(Player player){
        this.player=player;
    }
    @Override
    public void AI(float deltaTime) {
        decTimeToFire(deltaTime);
        incTimer(deltaTime);
        if(getTimer()>=getNextUpdate()){
            Bearing=(float)Math.atan2(getY()-player.getY(),player.getX()- getX());
            if (Bearing<0)Bearing=MathF.PI*2+Bearing;
            if(Bearing<(MathF.ThreePIOver4)) Bearing=MathF.ThreePIOver4;
            else if (Bearing>MathF.FivePIOver4) Bearing=MathF.FivePIOver4;
            //if(compassBearing)
            setAngle(Bearing);
            resetTimer();
            setNextUpdate(rnd.nextFloat()*2);
        }
        setPos(getX()+(FloatMath.cos(getAngle()) * (float)getSpeed()), getY() - (FloatMath.sin(getAngle()) * getSpeed()));
        if((getX() + (getFrame().width()*getScale()))<0) setActive(false);
    }

    @Override
    public void Update(float deltaTime) {
        if(isActive()){
            AI(deltaTime);
        }
    }

}
