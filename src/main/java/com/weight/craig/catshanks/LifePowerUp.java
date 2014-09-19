package com.weight.craig.catshanks;

/**
 * Created by Craig on 12/18/13.
 */
public class LifePowerUp extends PowerUp {
    private static final String Anim="Life";
    private Player player;
    public LifePowerUp(float x, float y,float decayTime,Player player) {
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2),
                AnimationManager.getInstance().getAnimationSet(Anim),Resolution.getInstance().getScale());
        this.player=player;
        setTimer(decayTime);//Adjust this later
        setPos(x,y);
    }

    /**
     * Used for loading...
     * @param x
     * @param y
     * @param decayTime
     */
    public LifePowerUp(float x, float y,float decayTime) {
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2),
                AnimationManager.getInstance().getAnimationSet(Anim),Resolution.getInstance().getScale());
        this.player=null;
        setTimer(decayTime);//Adjust this later
        setPos(x,y);
    }

    public void init_onLoad(Player player){
        this.player=player;
    }

    @Override
    public void AI(float deltaTime) {
        player.incLives();
        setActive(false);
    }
}
