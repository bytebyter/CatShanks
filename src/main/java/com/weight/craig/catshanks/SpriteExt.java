package com.weight.craig.catshanks;

import android.graphics.RectF;

/**
 * Created by Craig on 12/14/13.
 */
public abstract class SpriteExt extends Sprite {
    private float x, y,Scale=1.0f;
    private ShapeF CollisionBox;
    private int speed;

    public SpriteExt(int textureId) {
        super(textureId);
    }

    public float getX(){ return x;}
    public float getY() { return y; }
    public int getSpeed() { return speed;}
    public void  setSpeed(int speed){ this.speed=speed; }
    public void setScale(float Scale){ this.Scale=Scale; }
    public float getScale() { return this.Scale; }

    public void setX(float x) {
        this.x=x;
        adjustCollisionBox();
    }
    public void setY(float y) {
        this.y=y;
        adjustCollisionBox();
    }
    public void setPos(float x,float y){
        this.x=x;
        this.y=y;
        adjustCollisionBox();
    }
    public void incX(float x){
        this.y+=y;
        adjustCollisionBox();
    }
    public void incY(float y){
        this.x+=x;
        adjustCollisionBox();
    }
    public void incPos(float x, float y){
        this.x+=x;
        this.y+=y;
        adjustCollisionBox();
    }

    public ShapeF getColBox(){return CollisionBox;}
    private void adjustCollisionBox(){
        if(getCollisionBox().isCircle()){
            if(CollisionBox==null) CollisionBox=new ShapeF(new Circle(0,0,0));
            CollisionBox.getCircle().setAttribs(
                    getX()+ ((getFrame().width()* Scale) /2),
                    getY()+ ((getFrame().height() * Scale) /2),
                    (getCollisionBox().getCircle().radius() * Scale));
        }
        else{
            if(CollisionBox==null) CollisionBox=new ShapeF(new RectF(0,0,0,0));
            CollisionBox.getRect().set(
                    getX() + (getCollisionBox().getRect().left * Scale),
                    getY() + (getCollisionBox().getRect().top * Scale),
                    getX()+ (getCollisionBox().getRect().right * Scale),
                    getY()+ (getCollisionBox().getRect().bottom * Scale));
        }
    }
}
