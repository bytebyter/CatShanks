package com.weight.craig.catshanks.GameObjects.Enemies;

import android.util.FloatMath;
import com.weight.craig.catshanks.R;
import com.weight.craig.catshanks.Managers.TextureManager;
import com.weight.craig.catshanks.Managers.AnimationManager;
import com.weight.craig.catshanks.BaseObjects.Enemy;
import com.weight.craig.catshanks.Support.MathF;
import com.weight.craig.catshanks.Support.Resolution;
import java.util.Random;

/**
 * Created by Craig on 12/16/13.
 */
public class Robot extends Enemy {
    private Resolution resolution=Resolution.getInstance();

    private static final String Anim="Robot";
    private boolean WaveMovement=false;
    private Random rnd=new Random();
    private boolean onScreen=false;

    public Robot(float x){
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2),
                AnimationManager.getInstance().getAnimationSet(Anim),Resolution.getInstance().getScale());
        this.WaveMovement=true;
        setHp(20);
        setScoreValue(100);
        addWeapon(com.weight.craig.catshanks.Weapon.BladeSaw);
        setWeapon(com.weight.craig.catshanks.Weapon.BladeSaw);
        setFireRange(MathF.ThreePIOver4,MathF.FivePIOver4);
        setFireRate(1.5f);
        setSpeed(1 + rnd.nextInt(2));
        setPos(x,((resolution.getHeight()-(getFrame().height()*getScale()))/2f));
        setActive(true);

    }

    public Robot(float x, float y, float Angle, float AngleChange){
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2),
                AnimationManager.getInstance().getAnimationSet(Anim),Resolution.getInstance().getScale());
        this.WaveMovement=false;

        setHp(20);
        setScoreValue(100);
        addWeapon(com.weight.craig.catshanks.Weapon.BladeSaw);
        setWeapon(com.weight.craig.catshanks.Weapon.BladeSaw);
        setFireRange(MathF.ThreePIOver4,MathF.FivePIOver4);
        setFireRate(1.5f);
        setSpeed(1 + rnd.nextInt(2));
        setAngle(Angle);
        setAngularChange(AngleChange);
        setPos(x,y);
        setDestroyOnCollide(true);
        setActive(true);
    }

    /**
     * Used for loading...
     * @param x
     * @param y
     * @param Angle
     * @param AngleChange
     * @param waveMovement
     * @param speed
     * @param Hp
     * @param timeToFire
     */
    public Robot(float x, float y, float Angle, float AngleChange, boolean waveMovement, int speed, int Hp, float timeToFire){
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2),
                AnimationManager.getInstance().getAnimationSet(Anim),Resolution.getInstance().getScale());
        this.WaveMovement=waveMovement;
        setHp(Hp);
        setScoreValue(100);
        addWeapon(com.weight.craig.catshanks.Weapon.BladeSaw);
        setWeapon(com.weight.craig.catshanks.Weapon.BladeSaw);
        setFireRange(MathF.ThreePIOver4,MathF.FivePIOver4);
        setFireRate(1.5f);
        setSpeed(speed);
        setAngle(Angle);
        setAngularChange(AngleChange);
        setPos(x,y);
        setTimetoFire(timeToFire);
        setDestroyOnCollide(true);
        setActive(true);
    }

    @Override
    public void AI(float deltaTime) {
        decTimeToFire(deltaTime);
        if(WaveMovement){
            setPos(getX() - getSpeed(),(resolution.getHeight()/2)-((getFrame().height()*getScale()) * 1.5f)
                    * FloatMath.sin((2*(float)Math.PI) * (getX()/resolution.getWidth())));
        }
        else{
            updateMovementPath();
            setPos(getX()+(FloatMath.cos(getAngle()) * (float)getSpeed()), getY()- (FloatMath.sin(getAngle()) * getSpeed()));


        }
        if(!onScreen){
            if(getX()>0  & getX()<Resolution.getInstance().getResolution().x &
                getY()>0 & getY() < Resolution.getInstance().getResolution().y) onScreen=true;
        }
        else{
            //If offscreen after initial onscreen then allow removal.
            if(getX()+ (getFrame().width() * getScale()) <0) setActive(false);
            else if (getX()>Resolution.getInstance().getResolution().x) setActive(false);
            else if (getY() + (getFrame().height() *getScale()) <0) setActive(false);
            else if (getY()> Resolution.getInstance().getResolution().y) setActive(false);
        }
    }

    @Override
    public void Update(float deltaTime) {
        //Log.i("Enemy","x: " + getX() + " y: " + getY() );
        if(isActive()){
            AI(deltaTime);
        }
    }

    public boolean getInWaveFormation(){
        return WaveMovement;
    }
}
