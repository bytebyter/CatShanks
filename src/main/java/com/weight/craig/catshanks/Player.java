package com.weight.craig.catshanks;

import java.util.ArrayList;


/**
 * Created by Craig on 12/10/13.
 */
public class Player extends SpriteExt  {
    private static final String PlayerAnim="Cat";
    private AnimationManager animationManager=AnimationManager.getInstance();
    private int MaxHP, Hp,Lives=3;

    private ArrayList<Integer> Weapons=new ArrayList<Integer>();
    private int Weapon;
    private float TimeTilFire=0;
    private boolean WeaponFiring=false;

    private boolean Invincible=false;
    private boolean Active=true;
    private float iTimeLeft=0;

    public Player(int Hp){
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet1));
        for(String Anim: animationManager.getAnimationSet(PlayerAnim).keySet()){
            addAnimation(Anim,animationManager.getAnimationSet(PlayerAnim).get(Anim));
        }
        this.setScale(Resolution.getInstance().getScale());
        setAnimation(SpriteAnimation.DefaultAnim);
        Weapons.add(com.weight.craig.catshanks.Weapon.HairBall);
        Weapons.add(com.weight.craig.catshanks.Weapon.ClawLeft);
        Weapons.add(com.weight.craig.catshanks.Weapon.Rainbow);

        Weapon=0;
        this.MaxHP=Hp;
        this.Hp=Hp;
    }

    public Player(float X,float Y, int Hp,int MaxHP, int Lives,int WeaponID){
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet1));
        for(String Anim: animationManager.getAnimationSet(PlayerAnim).keySet()){
            addAnimation(Anim,animationManager.getAnimationSet(PlayerAnim).get(Anim));
        }
        this.setScale(Resolution.getInstance().getScale());
        setAnimation(SpriteAnimation.DefaultAnim);
        Weapons.add(com.weight.craig.catshanks.Weapon.HairBall);
        Weapons.add(com.weight.craig.catshanks.Weapon.ClawLeft);
        Weapons.add(com.weight.craig.catshanks.Weapon.Rainbow);

        Weapon=WeaponID;
        this.MaxHP=MaxHP;
        this.Hp=Hp;
        this.Lives=Lives;
        setPos(X,Y);
    }

    /**
     * Used for loading.
     * @param x
     * @param y
     * @param Lives
     * @param Hp
     * @param maxHP
     * @param weapon
     * @param timeTilFire
     * @param iTimeLeft
     * @param weaponFiring
     * @param Invincible
     * @param Active
     */
    public Player(float x, float y,int Lives,int Hp,int maxHP, int weapon, float timeTilFire,float iTimeLeft,boolean weaponFiring,boolean Invincible,boolean Active){
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet1));
        for(String Anim: animationManager.getAnimationSet(PlayerAnim).keySet()){
            addAnimation(Anim,animationManager.getAnimationSet(PlayerAnim).get(Anim));
        }
        this.setScale(Resolution.getInstance().getScale());
        setAnimation(SpriteAnimation.DefaultAnim);
        Weapons.add(com.weight.craig.catshanks.Weapon.HairBall);
        Weapons.add(com.weight.craig.catshanks.Weapon.ClawLeft);
        Weapons.add(com.weight.craig.catshanks.Weapon.Rainbow);

        this.Lives=Lives;
        this.MaxHP=maxHP;
        this.Hp=Hp;
        this.Weapon=weapon;
        this.WeaponFiring=weaponFiring;
        this.Invincible=Invincible;
        this.iTimeLeft=iTimeLeft;

        this.setPos(x,y);
        this.Active=Active;
    }

    public void decLives(){ Lives--;}
    public void incLives(){ Lives++;}
    public void setLives(int lives){ this.Lives=lives;}
    public int getLives(){ return Lives; }
    public int getMaxHP(){ return MaxHP; }


    public int getHp(){ return Hp; }
    public void incHp(int amount){
        Hp+=amount;
        if (Hp>MaxHP) Hp=MaxHP;
        else if (Hp<0) Hp=0;
    }
    public void resetHP(){Hp=MaxHP;}

    @Override
    public void Update(float deltaTime) {
        updateTimeToFire(deltaTime);
        //dec invincibility timer if applicable;
        if(Invincible){
            iTimeLeft-=deltaTime;
            if(iTimeLeft<=0) Invincible=false;
        }
    }

    public void incWeapon(){
        if(Weapon+1>=Weapons.size()) { Weapon=0; }
        else Weapon++;
    }
    public void decWeapon(){
        if(Weapon-1 < 0) Weapon=Weapons.size()-1;
        else Weapon--;
    }

    public void setActive(boolean Active){ this.Active=Active; }
    public boolean isActive(){ return this.Active;}

    public boolean isInvincible(){ return Invincible;}
    public void InitInvincibility(float time){
        Invincible=true;
        iTimeLeft=time;
    }
    public void addWeapon(int weaponID){
        if(!Weapons.contains(weaponID) & WeaponsManager.getInstance().WeaponExists(weaponID)){
            Weapons.add(weaponID);
        }
    }
    public void setWeapon(int weaponID){
        if(Weapons.contains(weaponID)) Weapon=weaponID;
    }
    public int getWeaponCount(){ return Weapons.size(); }
    public int getWeaponIndex(){ return Weapon; }
    public ArrayList<Integer> getWeapons(){ return Weapons; }
    public void setHp(int Hp){ this.Hp=Hp; }
    public int GetWeapon(){ return Weapons.get(Weapon); }

    public void setWeaponFiring(boolean firing){ this.WeaponFiring=firing;}
    public boolean getCanWeaponFire(){ return (TimeTilFire<=0);}
    public void FireWeapon(){ TimeTilFire=WeaponsManager.getInstance().getWeapon(GetWeapon()).getFireRate(); }
    public boolean isWeaponFiring(){ return WeaponFiring; }
    private void updateTimeToFire(float timeTilFire){
        TimeTilFire-=timeTilFire;
    }

}
