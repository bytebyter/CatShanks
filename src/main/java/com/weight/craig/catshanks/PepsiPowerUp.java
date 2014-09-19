package com.weight.craig.catshanks;

/**
 * Created by Craig on 12/18/13.
 */
public class PepsiPowerUp extends PowerUp {
    private static final String Anim="Pepsi";
    Player player;
    public PepsiPowerUp(float x, float y,float decayTime,Player player) {
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2),
                AnimationManager.getInstance().getAnimationSet(Anim),Resolution.getInstance().getScale());

        this.player=player;
        setTimer(decayTime);
        setPos(x,y);
        setActive(true);
    }

    /**
     * Used for loading...
     * @param x
     * @param y
     * @param decayTime
     */
    public PepsiPowerUp(float x, float y,float decayTime) {
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2),
                AnimationManager.getInstance().getAnimationSet(Anim),Resolution.getInstance().getScale());

        this.player=null;
        setTimer(decayTime);
        setPos(x,y);
        setActive(true);
    }

    public void init_onLoad(Player player){
        this.player=player;
    }

    @Override
    public void AI(float deltaTime) {
        player.incHp(25);
        setActive(false);
    }
}
