package com.weight.craig.catshanks.Managers;

import com.weight.craig.catshanks.R;
import com.weight.craig.catshanks.BaseObjects.Weapon;

import java.util.HashMap;

/**
 * Created by Craig on 12/15/13.
 */
public class WeaponsManager {
    TextureManager textureManager= TextureManager.getInstance();
    private static WeaponsManager instance;

    private final String ClawLeftAnim="Claw-Left";
    private final String DefaultAnimation="Default";
    private final String RainbowAnim="Rainbow-Beam";
    private final String CannonBallAnim="Cannon-Ball";
    private final String BladeSawAnim="Blade-Saw";
    private final String GarbageAnim="Garbage";
    private final String ClawAnim="Claw";
    private final String HairballAnim="Hairball";
    private final String BoxAnim="Box";


    private HashMap<Integer,String> WeaponsMap=new HashMap<Integer, String>();
    private HashMap<String,Weapon> Weapons=new HashMap<String, Weapon>();

    public static synchronized WeaponsManager getInstance(){
        if(instance==null){
            instance=new WeaponsManager();
        }
        return instance;
    }
    private WeaponsManager(){
        initWeapons();
    }
    public boolean WeaponExists(int WeaponId){
        if (WeaponsMap.containsKey(WeaponId)) return true;
        return false;
    }
    public Weapon getWeapon(int WeaponId){
        if(WeaponsMap.containsKey(WeaponId)) return Weapons.get(WeaponsMap.get(WeaponId));
        return null;
    }

    private void initWeapons(){
        //Hairball
        WeaponsMap.put(Weapon.HairBall,HairballAnim);
        Weapons.put(HairballAnim,new Weapon(textureManager.getResourceTextureID(R.drawable.tilesheet2),HairballAnim,DefaultAnimation,1f,2,true,10,2));
        //Bladesaw
        WeaponsMap.put(Weapon.BladeSaw,BladeSawAnim);
        Weapons.put(BladeSawAnim,new Weapon(textureManager.getResourceTextureID(R.drawable.tilesheet2),BladeSawAnim,DefaultAnimation,0.15f,2,true,5,5));
        //Rainbow
        WeaponsMap.put(Weapon.Rainbow,RainbowAnim);
        Weapons.put(RainbowAnim,new Weapon(textureManager.getResourceTextureID(R.drawable.tilesheet2),RainbowAnim,DefaultAnimation,3f,4,false,5,1));
        //Claw Weapon
        WeaponsMap.put(Weapon.Claw,ClawAnim);
        Weapons.put(ClawAnim,new Weapon(textureManager.getResourceTextureID(R.drawable.tilesheet2),ClawAnim,DefaultAnimation,1f,4,true,20,2));
        //Box Weapon
        WeaponsMap.put(Weapon.Box,BoxAnim);
        Weapons.put(BoxAnim,new Weapon(textureManager.getResourceTextureID(R.drawable.tilesheet2),BoxAnim,DefaultAnimation,2f,4,true,10,2));
        //CannonBall Weapon
        WeaponsMap.put(Weapon.CannonBall,CannonBallAnim);
        Weapons.put(CannonBallAnim,new Weapon(textureManager.getResourceTextureID(R.drawable.tilesheet2),CannonBallAnim,DefaultAnimation,0.2f,5,true,5,4));
        //Garbage Weapon
        WeaponsMap.put(Weapon.Garbage,GarbageAnim);
        Weapons.put(GarbageAnim,new Weapon(textureManager.getResourceTextureID(R.drawable.tilesheet2),GarbageAnim,DefaultAnimation,3f,4,true,15,2));
        //Claw-Left
        WeaponsMap.put(Weapon.ClawLeft,ClawLeftAnim);
        Weapons.put(ClawLeftAnim,new Weapon(textureManager.getResourceTextureID(R.drawable.tilesheet2),ClawLeftAnim,DefaultAnimation,2f,4,true,20,2));
    }
}
