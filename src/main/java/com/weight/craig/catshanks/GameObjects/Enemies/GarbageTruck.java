package com.weight.craig.catshanks.GameObjects.Enemies;

import android.util.FloatMath;
import com.weight.craig.catshanks.R;
import com.weight.craig.catshanks.Managers.TextureManager;
import com.weight.craig.catshanks.Managers.AnimationManager;
import com.weight.craig.catshanks.Support.MathF;
import com.weight.craig.catshanks.Support.Resolution;
import com.weight.craig.catshanks.BaseObjects.Enemy;

import java.util.Random;

/**
 * Created by Craig on 12/15/13.
 */
public class GarbageTruck extends Enemy {
    private static final String Anim="Garbage-Truck";
    private Resolution resolution=Resolution.getInstance();
    private Random rnd=new Random();
    private float removalTimer,removalTime;
    private boolean atCenter=false;

    public GarbageTruck(int Speed){
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet1),
                AnimationManager.getInstance().getAnimationSet(Anim),Resolution.getInstance().getScale());
        addWeapon(com.weight.craig.catshanks.Weapon.Garbage);
        addWeapon(com.weight.craig.catshanks.Weapon.Box);
        setWeapon(com.weight.craig.catshanks.Weapon.Garbage);

        setFireRange(MathF.ThreePIOver4,MathF.FivePIOver4);
        setFireRate(3f);

        setSpeed(Speed);
        setHp(120);
        setScoreValue(500);
        setDestroyOnCollide(false);
        setActive(true);
        init();
    }

    /**
     * Used for loading...
     * @param x
     * @param y
     * @param Speed
     * @param angle
     * @param Hp
     * @param timeToFire
     * @param remTimer
     * @param remTime
     * @param atCenter
     * @param nextUpdate
     */
    public GarbageTruck(float x, float y, int Speed, float angle, int Hp, float timeToFire, float remTimer, float remTime, boolean atCenter, float nextUpdate){
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet1),
                AnimationManager.getInstance().getAnimationSet(Anim),Resolution.getInstance().getScale());
        addWeapon(com.weight.craig.catshanks.Weapon.Garbage);
        addWeapon(com.weight.craig.catshanks.Weapon.Box);
        setWeapon(com.weight.craig.catshanks.Weapon.Garbage);

        this.removalTime=remTime;
        this.removalTimer=remTimer;
        this.atCenter=atCenter;

        setFireRange(MathF.ThreePIOver4,MathF.FivePIOver4);
        setFireRate(3f);

        setSpeed(Speed);
        setHp(Hp);
        setScoreValue(500);

        setTimetoFire(timeToFire);
        setAngle(angle);
        setPos(x,y);
        setNextUpdate(nextUpdate);
        setDestroyOnCollide(false);
        setActive(true);
    }

    private void init(){

        setPos((resolution.getWidth()-((getFrame().width() *getScale()) *2))/2,resolution.getHeight());
        setAngle(0.8f);

        removalTime=(7 + (rnd.nextFloat() * 15));
        setNextUpdate(rnd.nextFloat() * 2);
    }

    @Override
    public void AI(float deltaTime) {
        decTimeToFire(deltaTime);
        if(!atCenter){
            setPos(getX()+(FloatMath.cos(getAngle()) * (float)getSpeed()), getY() - (FloatMath.sin(getAngle()) * getSpeed()));
            if(Math.abs((getY()+((getFrame().height()*getScale()/2)))-(resolution.getHeight()/2)) <= 8) {
                atCenter=true;
                setAngle( (rnd.nextFloat() * MathF.PI) + MathF.PIOver2);
            }
        }
        else{
            //Perform random Movement variance until removal time;
            if (removalTimer<removalTime){
                removalTimer+=deltaTime;

                setPos(getX()+(FloatMath.cos(getAngle()) * (float)getSpeed()), getY() - (FloatMath.sin(getAngle()) * getSpeed()));

                incTimer(deltaTime);
                if(getTimer()>=getNextUpdate()){
                    if(getY()<0) setAngle(MathF.PI + (rnd.nextFloat() * MathF.PI));
                    else if(getX()<resolution.getWidth()/2) setAngle(MathF.PIOver2 - (rnd.nextFloat() * MathF.PI));
                    else if(getX()+((getFrame().width()*getScale())/2)>resolution.getWidth()) setAngle(MathF.PIOver2 + (rnd.nextFloat() * MathF.PI));
                    else if(getY()>resolution.getHeight()) setAngle(rnd.nextFloat() * MathF.PI);
                    else setAngle(rnd.nextFloat() * MathF.Pi2);
                    if(getAngle()<0) setAngle(MathF.Pi2 + getAngle());


                    //Change Garbage type randomly
                    setWeaponIndex(rnd.nextInt(getWeaponCount()));
                    resetTimer();
                    setNextUpdate(rnd.nextFloat()*2);
                }
                //Set the exit angle.
                if(removalTimer>=removalTime){ setAngle(0); }
            }
            //Once it is removal time, follow removal logic
            else if(removalTimer>removalTime){
                setPos(getX()+(FloatMath.cos(getAngle()) * (float)getSpeed()), getY() - (FloatMath.sin(getAngle()) * getSpeed()));
                if(getX()>resolution.getWidth()) setActive(false);
            }
        }
    }


    @Override
    public void Update(float deltaTime) {
        if(isActive()){
            AI(deltaTime);
        }
    }

    public float getTimeToRemoval(){
        return removalTimer;
    }
    public float getRemovalTime(){
        return removalTime;
    }
    public boolean isAtCenter(){
        return atCenter;
    }
}
