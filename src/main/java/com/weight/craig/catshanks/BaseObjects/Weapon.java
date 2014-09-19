package com.weight.craig.catshanks.BaseObjects;

/**
 * Created by Craig on 12/14/13.
 */
public class Weapon {
    public static final int HairBall=0;
    public static final int Rainbow=1;
    public static final int BladeSaw=2;
    public static final int CannonBall=3;
    public static final int Claw=4;
    public static final int Garbage=5;
    public static final int Box=6;
    public static final int ClawLeft=7;

    private int TextureId=-1;
    private String AnimationId, Animation;
    private float FireRate=0, decayRate=0;
    private boolean ProjectilesRemovedOnImpact=true;
    private int ProjectilesStrength=1, ProjectileSpeed=1;

    public Weapon(int TextureId, String AnimationId, String Animation,
                  float FireRate, float decayRate, boolean RemovedOnImpact,
                  int ProjectileStrength, int ProjectileSpeed){
        this.TextureId=TextureId;
        this.AnimationId=AnimationId;
        this.Animation=Animation;
        this.FireRate=FireRate;
        this.decayRate=decayRate;
        this.ProjectilesRemovedOnImpact=RemovedOnImpact;
        this.ProjectilesStrength=ProjectileStrength;
        this.ProjectileSpeed=ProjectileSpeed;
    }

    public float getDecayRate(){ return decayRate; }
    public float getFireRate(){ return FireRate; }
    public String getAnimationId(){ return AnimationId; }
    public String getAnimation(){ return Animation; }
    public boolean removedOnImpact(){ return ProjectilesRemovedOnImpact;}
    public int getProjectileStrength(){ return ProjectilesStrength;}
    public int getProjectileSpeed(){ return ProjectileSpeed; }

}
