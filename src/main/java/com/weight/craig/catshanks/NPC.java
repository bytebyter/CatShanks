package com.weight.craig.catshanks;

/**
 * Created by Craig on 12/10/13.
 */
public abstract class NPC extends SpriteExt {
    private boolean destroyOnCollide=true;
    private float angle=0;
    private float deltaAngle=0;

    public NPC(int textureId) {
        super(textureId);
    }
    public abstract void AI(float deltaTime);

    //Lazy addition for AI. (Note does not have to be used, everything can be done in AI.)
    public void setDestroyOnCollide(boolean b) { destroyOnCollide=b; }
    public boolean getDestroyOnCollide() { return destroyOnCollide; }
    public void setAngle (float angle){ this.angle=angle; }
    public float getAngle(){ return angle;}
    public void setAngularChange(float deltaAngle){ this.deltaAngle=deltaAngle;}
    public float getAngularChange(){ return deltaAngle;}
    public void updateMovementPath(){ angle+=deltaAngle; }
}
