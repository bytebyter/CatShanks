package com.weight.craig.catshanks;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Craig on 12/10/13.
 */
public abstract class Enemy extends NPC {
    private int HP,ScoreValue=0;

    private ArrayList<Integer> Weapons= new ArrayList<Integer>();
    private int Weapon=0;
    private float TimeTilFire=0,FireRate=0;          //Time elapsed since the last fire occurred..
    private float rangeLow=0,rangeHi=0;
    private boolean autoAim=false;

    private boolean Active=true;
    private float timer,nextUpdate;


    public Enemy(int textureId,HashMap<String,SpriteAnimation> animationSet,float Scale) {
        super(textureId);
        for(String animation:animationSet.keySet()){
            addAnimation(animation,animationSet.get(animation));
        }
        setAnimation("Default");
        setScale(Scale);
    }

    public void setFireRange(float rangeLow,float rangeHi){
        this.rangeLow=rangeLow;
        this.rangeHi=rangeHi;
    }
    public float getFireRangeBeginning(){ return rangeLow;}
    public float getFireRangeEnd() { return rangeHi;}

    public void addWeapon(int WeaponId){
        if(WeaponsManager.getInstance().WeaponExists(WeaponId) & !Weapons.contains(WeaponId)) Weapons.add(WeaponId);
    }

    public Weapon getWeapon(){
        return WeaponsManager.getInstance().getWeapon(Weapon);
    }
    public int getWeaponCount(){ return Weapons.size(); }

    public void setWeapon(int WeaponId){
        if(Weapons.contains(WeaponId)) Weapon=WeaponId;
    }

    public void setWeaponIndex(int weapon){
        if(weapon<Weapons.size()) Weapon=Weapons.get(weapon);
    }
    public void setActive(boolean active){ this.Active=active; }
    public boolean isActive(){ return this.Active; }
    public void setNextUpdate(float nextUpdate){ this.nextUpdate=nextUpdate; }
    public float getNextUpdate(){ return nextUpdate; }
    public void resetTimer(){ timer=0; }
    public void incTimer(float increment){ timer+=increment;}
    public float getTimer(){ return timer; }

    public int getHp(){ return HP;}
    public void setHp(int Hp){ this.HP=Hp; }
    public void decHp(int Hp){
        this.HP-=Hp;
        if(HP<=0) Active=false;
    }

    public void setAutoAim(boolean autoAim){ this.autoAim=autoAim; }
    public boolean getAutoAim(){ return autoAim; }
    public void decTimeToFire(float amount){ TimeTilFire-=amount; }
    public void setTimetoFire(float time){ this.TimeTilFire=time; }
    public float getTimeToFire() { return TimeTilFire; }

    public boolean isReadyToFire(){return (TimeTilFire<=0);}
    public void resetTimeToFire(){ TimeTilFire=FireRate; }
    public void setFireRate(float fireRate){ this.FireRate=fireRate; }
    public void setScoreValue(int ScoreValue){ this.ScoreValue=ScoreValue; }
    public int getScoreValue(){ return ScoreValue; }
}
