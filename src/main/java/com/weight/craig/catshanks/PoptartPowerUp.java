package com.weight.craig.catshanks;

/**
 * Created by Craig on 12/18/13.
 */
public class PoptartPowerUp extends PowerUp {
    private Player player;
    private final static String Anim="Pop-Tart";

    public PoptartPowerUp(float x, float y,float decayTime,Player player) {
        super(TextureManager.getInstance().getResourceTextureID(R.drawable.tilesheet2),
                AnimationManager.getInstance().getAnimationSet(Anim),Resolution.getInstance().getScale());
        this.player=player;
        setTimer(decayTime);//Adjust this later
        setPos(x,y);

    }

    /**
     * Used for Loading..
     * @param x
     * @param y
     * @param decayTime
     */
    public PoptartPowerUp(float x, float y,float decayTime) {
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
        player.addWeapon(Weapon.Rainbow);
        setActive(false);
    }
}
