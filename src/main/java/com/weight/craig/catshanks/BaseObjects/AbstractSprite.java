package com.weight.craig.catshanks.BaseObjects;

import android.graphics.RectF;
import android.util.SparseArray;

import com.weight.craig.catshanks.Collision.Circle;
import com.weight.craig.catshanks.Collision.ShapeF;
import com.weight.craig.catshanks.Managers.TextureManager;

@SuppressWarnings("unused")
/**
 * An abstract sprite, is the base from which all sprite objects are created
 * Created by Craig on 8/30/2014.
 */

public abstract class AbstractSprite {
    private TextureManager TM=TextureManager.getInstance();

    //Animation Control Variables
    private SparseArray<SpriteAnimation> Animations=new SparseArray<SpriteAnimation>();
    private static final int NO_ANIMATIONS=-1;
    private int Animation=-1;
    private int TextureRefId;

    //Frame Control Variables
    private float partialFrame=0;
    private int Frame=0;
    private int FPS=0;
    private boolean isAnimationLooping =false;
    private boolean isAnimationCompleted=false;
    private boolean SwapAnimationUponComplete=false;
    private int SwapAnimationID=-1;

    //Sprite Positioning/Attribute Variables
    private float x, y,Scale=1.0f;
    private ShapeF CollisionBox;
    private int Speed;
    private boolean isActive=true;


    /**
     * Creates an abstract Sprite
     * @param textureId Unique Id identifying which texture to use for the sprite.
     */
    public AbstractSprite(int textureId){
        setTextureId(textureId);
    }

    /**
     * Sets the animation frame rate.
     * @param fps Frame Rate
     */
    public void setFPS(int fps){ this.FPS=fps; }

    /**
     * Gets the current animation source frame rectangle.
     * @return Current animation source frame rectangle.
     */
    public RectF getFrame(){
        if(Frame>=Animations.get(Animation).getFrameCount()) Frame=0;
        return Animations.get(Animation).getFrame(Frame);
    }


    /**
     * Adds an animation to the current sprite.
     * @param AnimationID Unique identifier for the animation being added.
     * @param Animation An animation object containing all of the frame data for the given animation.
     */
    public void addAnimation(int AnimationID,SpriteAnimation Animation){
        if (Animation!=null & AnimationID >= 0)
            if(Animation.getFrameCount()!=0) Animations.put(AnimationID,Animation);
    }

    /**
     * Removes an animation from the given sprites animation list.
     * @param AnimationID Unique identifier for the animation being removed.
     */
    public void removeAnimation(int AnimationID){
        Animations.remove(AnimationID);
    }

    /**
     * Updates the frame for the sprite.
     * @param deltaTime Time elapsed since last frame update.
     */
    public void FrameUpdate(float deltaTime){
        partialFrame+=(float) FPS* deltaTime;
        if(partialFrame>=1){
            partialFrame=0;
            incFrame();
        }
    }

    /**
     * Sets the animation in use by the sprite, and then sets it to loop.
     * @param Animation Unique identifier for the animation being switched to.
     */
    public void setAnimation_Looping(int Animation){
        if(Animations.indexOfKey(Animation)>-1) {
            this.isAnimationLooping=true;
            this.SwapAnimationUponComplete=false;
            this.isAnimationCompleted=false;
            this.Animation=Animation;
        }
    }

    /**
     * Sets the animation in use by the sprite, and then sets it to not looping.
     * @param Animation Unique identifier for the animation being switched to.
     */
    public void setAnimation_NonLooping(int Animation){
        if(Animations.indexOfKey(Animation)>-1) {
                this.isAnimationLooping = false;
                this.SwapAnimationUponComplete = false;
                this.isAnimationCompleted=false;
                this.Animation = Animation;
        }
    }


    /**
     * Sets the animation in use to BeginningAnimation, and then after that animation has finished,
     * the animation is switched over to EndAnimation. The end animation will then loop if set to do so.
     * @param BeginningAnimation Unique Id for the starting Animation.
     * @param EndAnimation Unique Id for the ending Animation.
     * @param isEndAnimationLooping Sets up whether the ending animation will loop.
     */
    public void setAnimation_Swap(int BeginningAnimation, int EndAnimation,boolean isEndAnimationLooping){
        if(Animations.indexOfKey(BeginningAnimation)>-1 && Animations.indexOfKey(EndAnimation)>-1) {
            this.SwapAnimationUponComplete=true;
            this.isAnimationCompleted=false;
            this.isAnimationLooping =isEndAnimationLooping;
            this.SwapAnimationID=EndAnimation;
            this.Animation=BeginningAnimation;
        }
    }

    /**
     * Gets the current Texture ID.
     * @return Unique Id of the texture being used by the current sprite.
     */
    public int getTexture(){ return TextureRefId;}

    /**
     * Sets the current texture in use by the sprite.
     * @param textureID Unique Identifier of the texture that the sprite is being setup to use.
     */
    public void setTextureId(int textureID){
        if (TM.containsTexture(textureID)) this.TextureRefId =textureID;
    }

    /**
     * Sets the texture resource Id for the given texture.
     * If the texture resource has not been loaded, this function automatically loads
     * the texture resource.
     * @param textureRes Unique Texture Resource ID
     */
    public void setTextureRes(int textureRes){
        int textureId=TM.getResourceTextureID(textureRes);
        if(textureId!=-1) TextureRefId = textureId ;
        else {
            textureId= TM.addTexture(textureRes);
            if (textureId!=-1) TextureRefId =textureId;
        }
    }

    /**
     * Increments the current frame.
     * Todo:Optimize this!!
     */
    private void incFrame(){
        if(SwapAnimationUponComplete){
            if(Frame < Animations.get(Animation).getFrameCount()) Frame++;
            else{
                SwapAnimationUponComplete=false;
                Frame=0;
                Animation=SwapAnimationID;
            }
        }
        else{
            if(isAnimationLooping) {
                if (Frame < Animations.get(Animation).getFrameCount()) Frame++;
                else Frame=0;
            }
            else{
                if(!isAnimationCompleted){
                    if (Frame < Animations.get(Animation).getFrameCount()) Frame++;
                    else {
                        isAnimationCompleted=true;
                        Frame=0;
                    }
                }
            }
        }
    }

    /**
     * Updates the object.
     * @param deltaTime Time elapsed since last sprite update.
     */
    public abstract void Update(float deltaTime);

    /**
     * Gets the x coordinate for the current sprite.
     * @return The x coordinate for the current sprite.
     */
    public float getX(){ return x;}

    /**
     * Gets the y coordinate for the current sprite.
     * @return The y coordinate for the current sprite.
     */
    public float getY() { return y; }

    /**
     * Gets the speed for the current sprite.
     * @return Speed of the current sprite.
     */
    public int getSpeed() { return Speed;}

    /**
     * Sets the speed of the current sprite.
     * @param speed Speed to set the current sprite to.
     */
    public void  setSpeed(int speed){ this.Speed =speed; }

    /**
     * Sets the scale for the current sprite.
     * @param Scale Scale to set the current sprite to.
     */
    public void setScale(float Scale){ this.Scale=Scale; }

    /**
     * Gets the scale of the current sprite.
     * @return Scale of the current sprite.
     */
    public float getScale() { return this.Scale; }

    /**
     * Sets the x position of the sprite.
     * @param x X position to set the sprite to.
     */
    public void setX(float x) { this.x=x; }

    /**
     * Sets the y position of the sprite.
     * @param y Sets the y position for the current sprite.
     */
    public void setY(float y) { this.y=y; }

    /**
     * Sets the position of the current sprite.
     * @param x X position.
     * @param y Y position.
     */
    public void setPos(float x,float y){
        this.x=x;
        this.y=y;
    }

    /**
     * Increments the x position of the sprite by a given value.
     * @param x Increment sprites X position by x.
     */
    public void incX(float x){ this.y+=y; }

    /**
     * Increments the y position of the sprite by a given value.
     * @param y Increment sprites Y position by y.
     */
    public void incY(float y){ this.x+=x; }

    /**
     * Increments the position of the sprite.
     * @param x Increment sprites X position by x.
     * @param y Increment sprites Y position by y.
     */
    public void incPos(float x, float y){
        this.x+=x;
        this.y+=y;
    }

    /**
     * Gets the collision box of the current sprite.
     * @return A shapeF object representing the collision box for the given sprite.
     */
    public ShapeF getCollisionBox(){
        if(Frame>=Animations.get(Animation).getFrameCount()) Frame=0;
        CollisionBox=Animations.get(Animation).getCollisionBox(Frame);

        if(CollisionBox==null) CollisionBox=new ShapeF(new Circle(0,0,0));

        if(CollisionBox.isCircle()){
            CollisionBox.getCircle().setAttribs(
                    getX()+ ((getFrame().width()* Scale) /2),
                    getY()+ ((getFrame().height() * Scale) /2),
                    (CollisionBox.getCircle().radius() * Scale));
        }
        else{
            CollisionBox.getRect().set(
                    getX() + (CollisionBox.getRect().left * Scale),
                    getY() + (CollisionBox.getRect().top * Scale),
                    getX()+ (CollisionBox.getRect().right * Scale),
                    getY()+ (CollisionBox.getRect().bottom * Scale));
        }
        return CollisionBox;
    }
    public boolean isActive(){ return this.isActive; }
    public void setActive(boolean active){ this.isActive=active; }
}
