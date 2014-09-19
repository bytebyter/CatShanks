package com.weight.craig.catshanks.BaseObjects;

import android.util.FloatMath;
import com.weight.craig.catshanks.Managers.AnimationManager;
import com.weight.craig.catshanks.R;
import com.weight.craig.catshanks.Support.Resolution;
import com.weight.craig.catshanks.Managers.TextureManager;


/**
 * Created by Craig on 12/10/13.
 */
public class Projectile extends NPC {
    private int strength=0;
    private boolean Active=false;
    private float noHit=0;
    private String weaponID=null;

    public Projectile(float x, float y, float angle, int Speed, Weapon weapon){
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2));
        //for(String animation:AnimationManager.getInstance().getAnimationSet(weapon.getAnimationId()).keySet()){
        //    addAnimation(animation,AnimationManager.getInstance().getAnimationSet(weapon.getAnimationId()).get(animation));
        //}
        weaponID=weapon.getAnimationId();

        //setAnimation("Default");
        setScale(Resolution.getInstance().getScale());
        this.strength=weapon.getProjectileStrength();
        setAngle(angle);
        setSpeed(Speed + weapon.getProjectileSpeed());
        setDestroyOnCollide(weapon.removedOnImpact());
        setFPS(AnimationManager.getInstance().getAnimationSet(weapon.getAnimationId()).get(SpriteAnimation.DefaultAnim).getFrameCount() * 2);
        setPos(x,y);
        Active=true;
    }

    /**
     * Used for Loading...
     * @param x
     * @param y
     * @param angle
     * @param Speed
     * @param strength
     * @param DestroyOnCollide
     * @param weaponID
     */
    public Projectile(float x, float y, float angle, int Speed, int strength, boolean DestroyOnCollide, float timeTilCollidable, String weaponID){
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2));
        //for(String animation:AnimationManager.getInstance().getAnimationSet(weaponID).keySet()){
        //    addAnimation(animation,AnimationManager.getInstance().getAnimationSet(weaponID).get(animation));
        //}
        noHit=timeTilCollidable;
        this.weaponID=weaponID;

        //setAnimation("Default");
        setScale(Resolution.getInstance().getScale());
        this.strength=strength;
        setAngle(angle);
        setSpeed(Speed);
        setDestroyOnCollide(DestroyOnCollide);
        setFPS(AnimationManager.getInstance().getAnimationSet(weaponID).get(SpriteAnimation.DefaultAnim).getFrameCount() * 2);

        setPos(x,y);
        Active=true;
    }

    public String getWeaponId(){ return weaponID; }
    public int getStrength(){ return strength; }

    @Override
    public void AI(float deltaTime) {
        setPos(getX()+(FloatMath.cos(getAngle()) * (float)getSpeed()), getY() - (FloatMath.sin(getAngle()) * getSpeed()));
        if(getX()+ (getFrame().width() * getScale()) <0) setActive(false);
        else if (getX()>Resolution.getInstance().getResolution().x) setActive(false);
        else if (getY() + (getFrame().height() *getScale()) <0) setActive(false);
        else if (getY()> Resolution.getInstance().getResolution().y) setActive(false);
    }

    public void setActive(boolean active){ Active=active; }
    public boolean getActive(){ return Active; }

    @Override
    public void Update(float deltaTime) {
        if(Active) {
            AI(deltaTime);
            FrameUpdate(deltaTime);
            updateTimeTilCollidable(deltaTime);
        }
    }

    public void resetTimeToCollidable() { noHit=0.3f;}
    public void updateTimeTilCollidable(float deltatime){ noHit-=deltatime;}
    public float getTimeTilCollidable(){ return noHit;}
    public boolean isCollidable(){ return (noHit<=0);}
}
