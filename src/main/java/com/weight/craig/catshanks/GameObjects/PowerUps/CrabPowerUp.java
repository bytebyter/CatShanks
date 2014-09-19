package com.weight.craig.catshanks.GameObjects.PowerUps;

import com.weight.craig.catshanks.Managers.AnimationManager;
import com.weight.craig.catshanks.GameObjects.Players.Player;
import com.weight.craig.catshanks.BaseObjects.PowerUp;
import com.weight.craig.catshanks.R;
import com.weight.craig.catshanks.Support.Resolution;
import com.weight.craig.catshanks.Managers.TextureManager;
import com.weight.craig.catshanks.BaseObjects.Weapon;

/**
 * Created by Craig on 12/18/13.
 */
public class CrabPowerUp extends PowerUp {
    private Player player;
    private static final String Anim="Crab-Weapon";

    public CrabPowerUp(float x, float y, float decayTime, Player player) {
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
    public CrabPowerUp(float x, float y, float decayTime) {
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
        player.addWeapon(Weapon.ClawLeft);//Need to change this to Claw-Right eventually.
        setActive(false);
    }
}
