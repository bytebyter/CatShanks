package com.weight.craig.catshanks;

import android.os.Environment;
//import android.util.Log;
import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Craig on 12/10/13.
 */
public class GameModel {
    private Random rnd=new Random(System.currentTimeMillis());

    //Testing
    private Int_Buffer PlayerZones=new Int_Buffer();
    private HashMap<Integer,ArrayList<Enemy>> _Enemies=new HashMap<Integer, ArrayList<Enemy>>();
    private HashMap<Integer,ArrayList<Projectile>> _EnemyProjectiles=new HashMap<Integer, ArrayList<Projectile>>();
    private HashMap<Integer,ArrayList<Projectile>> _FriendlyProjectiles=new HashMap<Integer,ArrayList<Projectile>>();
    private HashMap<Integer,ArrayList<PowerUp>> _PowerUps=new HashMap<Integer,ArrayList<PowerUp>>();

    private int ZoneWidth=256,ZoneHeight=128;
    private int ZonesX=(int)Resolution.getInstance().getWidth() / this.ZoneWidth;
    private int [] ZoneBuffer=new int[4];
    private final int ZoneBufferTopLeft =0;
    private final int ZoneBufferTopRight =1;
    private final int ZoneBufferBottomLeft=2;
    private final int ZoneBufferBottomRight=3;

    //Active item lists.
    private ArrayList<Enemy> Enemies= new ArrayList<Enemy>();
    private ArrayList<Projectile> FriendlyProjectiles= new ArrayList<Projectile>();
    private ArrayList<Projectile> EnemyProjectiles= new ArrayList<Projectile>();
    private ArrayList<Particle> ParticleList=new ArrayList<Particle>();
    private ArrayList<PowerUp> PowerUps=new ArrayList<PowerUp>();

    //Removal lists.
    private HashSet<Enemy> EnemyRemovalList= new HashSet<Enemy>();
    private HashSet<Projectile> FriendlyProjectileRemoval=new HashSet<Projectile>();
    private HashSet<Projectile> EnemyProjectileRemoval=new HashSet<Projectile>();
    private HashSet<Particle> ParticleRemovalList=new HashSet<Particle>();
    private HashSet<PowerUp> PowerUpRemovalList=new HashSet<PowerUp>();

    private Player player;
    private float nextUpdate,timer,deathTimer=0;
    private float projectileangle=0.0f;
    private boolean playerdead=false;
    private int score=0;
    private boolean GameOver=false;
    private int hit1,fire,hit3,explosion,massExplosion,pUpSound;

    ObjSerializer objS=new ObjSerializer();
    Gson gson=objS.getGson();

    public GameModel(){}


    public void Initialize(){
        player=new Player(100);
        nextUpdate=0;
        onResume();
        hit1=Audio_Manager.getInstance().addSound("hit1.wav");
        fire=Audio_Manager.getInstance().addSound("hit2.wav");
        hit3=Audio_Manager.getInstance().addSound("hit3.wav");
        explosion=Audio_Manager.getInstance().addSound("explosion.wav");
        pUpSound=Audio_Manager.getInstance().addSound("powerup.wav");
        massExplosion=Audio_Manager.getInstance().addSound("massexplosion.wav");

    }

    public int getScore(){ return score; }
    public Player getPlayer(){ return this.player;}
    public ArrayList<Enemy> getEnemies(){ return Enemies; }
    public ArrayList<Projectile> getFriendlyProjectiles(){ return FriendlyProjectiles; }
    public ArrayList<Projectile> getEnemyProjectiles () { return EnemyProjectiles; }
    public ArrayList<PowerUp> getPowerUps(){ return PowerUps; }
    public ArrayList<Particle> getParticleList() { return ParticleList; }


    private void createEvents(){
        timer=0;
        if(Chance(90)){
            switch(rnd.nextInt(4)){
                case(0):
                    generateRobotWave(1+rnd.nextInt(4));
                    break;
                case(1):
                    generateRobotsAngle(MathF.ThreePIOver4 + (rnd.nextFloat() * MathF.PIOver2),1 + rnd.nextInt(4));
                    break;
                case(2):
                    generateSpaceSubmarines(1+ rnd.nextInt(3));
                    break;
                case(3):
                    generateSpaceCrabs(1 + rnd.nextInt(2));
                    break;
            }
        }
        else{
            generateGarbageTruck();
        }
        nextUpdate= 5+(rnd.nextFloat()*7);
    }

    //Generate sin wave robot group.
    private void generateRobotWave(int count){
        Robot robot=new Robot(Resolution.getInstance().getWidth());
        Enemies.add(robot);
        for(int i=1;i<count;i++){
            Enemies.add(new Robot(Resolution.getInstance().getWidth() + (i * (robot.getFrame().width() * robot.getScale() ))));
        }
    }

    //Generate robots that move at a given angle.
    private void generateRobotsAngle(float angle, int count){
        //float x,float y,float Angle,float AngleChange
        Robot robot=new Robot(Resolution.getInstance().getWidth(),Resolution.getInstance().getHeight()/2,angle,0);
        Enemies.add(robot);
        for(int i=1;i<count;i++){
            Enemies.add(new Robot(Resolution.getInstance().getWidth() + (i * (robot.getFrame().width() * robot.getScale())),Resolution.getInstance().getHeight()/2,angle,0));
        }
    }

    //generate space submarine enemies.
    private void generateSpaceSubmarines(int Count){
        SpaceSubmarine sub=new SpaceSubmarine(Resolution.getInstance().getWidth(),2);
        Enemies.add(sub);
        for(int i=1; i<Count;i++){
            Enemies.add(new SpaceSubmarine(Resolution.getInstance().getWidth() + (i * (sub.getFrame().width() * sub.getScale())),2));
        }
    }

    //Generate space crab enemies.
    private void generateSpaceCrabs(int Count){
        for(int i=0; i<Count;i++){
            switch(rnd.nextInt(2)){
                case(0):
                    //float x, float y,int Speed,Player player
                    Enemies.add(new SpaceCrab((Resolution.getInstance().getWidth()/2) + (rnd.nextFloat() * (Resolution.getInstance().getWidth()/2)),
                            0,2+(rnd.nextInt(2)),player));
                    break;
                case(1):
                    Enemies.add(new SpaceCrab((Resolution.getInstance().getWidth()/2) + (rnd.nextFloat() * (Resolution.getInstance().getWidth()/2)),
                            Resolution.getInstance().getHeight(),2+(rnd.nextInt(2)),player));
                    break;
            }
        }
    }
    private void generateGarbageTruck(){
        //Blowup all enemies on screen
        Audio_Manager.getInstance().playSound(massExplosion);
        for(int i=0;i<Enemies.size();i++){
            if(Enemies.get(i).isActive()){
                Audio_Manager.getInstance().playSound(explosion);
                generateDeathParticles(Enemies.get(i).getX() + (Enemies.get(i).getFrame().width() * Enemies.get(i).getScale()),
                        Enemies.get(i).getY() + (Enemies.get(i).getFrame().height() * Enemies.get(i).getScale()), 10);


            Enemies.get(i).setActive(false);
            EnemyRemovalList.add(Enemies.get(i));
            }
        }
        Enemies.add(new GarbageTruck(2 + rnd.nextInt(1)));
    }

    //Handle object cleanup.
    private void performCleanUp(){
        if(EnemyRemovalList.size()>10){
            for(Enemy enemy:EnemyRemovalList){
                Enemies.remove(enemy);
            }
            EnemyRemovalList.clear();
        }
        if(FriendlyProjectileRemoval.size() > 20){
            for(Projectile p:FriendlyProjectileRemoval){
                FriendlyProjectiles.remove(p);
            }
            FriendlyProjectileRemoval.clear();
        }
        if (EnemyProjectileRemoval.size()>20){
            for(Projectile p:EnemyProjectileRemoval){
                EnemyProjectiles.remove(p);
            }
            EnemyProjectileRemoval.clear();
        }
        if(ParticleRemovalList.size()>100){
            for(Particle p: ParticleRemovalList){
                ParticleList.remove(p);
            }
            ParticleRemovalList.clear();
        }

        if(PowerUps.size()>20){
            for(PowerUp p:PowerUpRemovalList){
                PowerUps.remove(p);
            }
            PowerUpRemovalList.clear();
        }
    }


    public void Update(float deltaTime){
        if(!GameOver){
            timer+=deltaTime;
            if(timer >=nextUpdate){
                createEvents();
            }
            if(playerdead){
                deathTimer-=deltaTime;
                if(deathTimer<0){
                    playerdead=false;
                    player.setActive(true);
                    player.InitInvincibility(1);
                }
            }
            //Remove unused objects.
            performCleanUp();

            //Update player/enemy/projectile animations/collision boxes/perform enemy Ai as applicable
            player.Update(deltaTime);
            //updatePlayersZones();

            if(!playerdead & player.getCanWeaponFire()){
                //float x, float y, float angle,int Speed, Weapon weapon
                FriendlyProjectiles.add(new Projectile(player.getX()+ (player.getFrame().width()*player.getScale()),
                        (player.getY() + ((player.getFrame().height()*player.getScale())/2)),0,
                        WeaponsManager.getInstance().getWeapon(player.GetWeapon()).getProjectileSpeed(),
                        WeaponsManager.getInstance().getWeapon(player.GetWeapon())));
                player.FireWeapon();
                Audio_Manager.getInstance().playSound(fire);

            }

            //_Enemies.clear();
            for(Enemy enemy: Enemies){
                if(enemy.isActive()){
                    if(enemy.isReadyToFire()){
                        //Create enemy projectiles
                        if(enemy.getAutoAim()){
                            float compassBearing=(float)Math.atan2(enemy.getY()-player.getY(),player.getX()-enemy.getX());
                            if (compassBearing<0)compassBearing=MathF.PI*2+compassBearing;

                            //Calculate firing angle here.
                            projectileangle= compassBearing;
                        }
                        else projectileangle= enemy.getFireRangeBeginning() + (rnd.nextFloat() * (enemy.getFireRangeEnd()-enemy.getFireRangeBeginning()));

                        EnemyProjectiles.add(new Projectile(enemy.getX(),enemy.getY(),projectileangle,enemy.getSpeed(),enemy.getWeapon()));
                        enemy.resetTimeToFire();
                    }
                    enemy.Update(deltaTime);
                    //updateEnemyZone(enemy);
                }
            }

            //_FriendlyProjectiles.clear();
            //Update friendly projectiles.
            for(Projectile projectile: FriendlyProjectiles) {
                if(projectile.getActive()) {
                    projectile.Update(deltaTime);
                    //updateProjectileZone(projectile,true);
                }
            }

            //_EnemyProjectiles.clear();
            //update enemy projectiles.
            for(Projectile projectile: EnemyProjectiles){
                if(projectile.getActive()) {
                    projectile.Update(deltaTime);
                    //updateProjectileZone(projectile,false);
                }
            }

            //Update particles, and add particles to removal list as necessary.
            for(int i=0; i<ParticleList.size();i++){
                if(ParticleList.get(i).isActive()) ParticleList.get(i).Update(deltaTime);
                else ParticleRemovalList.add(ParticleList.get(i));
            }

            //_PowerUps.clear();
            for(int i=0; i<PowerUps.size();i++){
                if(PowerUps.get(i).isActive()) {
                    PowerUps.get(i).Update(deltaTime);
                    //updatePowerUpZone(PowerUps.get(i));
                }
                else PowerUpRemovalList.add(PowerUps.get(i));
            }

            handleCollisions();
            //_handleCollisions();
        }
    }


    private void updateZoneBuffers(int left, int top, int right, int bottom){
        ZoneBuffer[this.ZoneBufferTopLeft]= left / this.ZoneWidth;
        ZoneBuffer[this.ZoneBufferTopLeft]= ZoneBuffer[this.ZoneBufferTopLeft] +
                (top / this.ZoneHeight * ZonesX);

        ZoneBuffer[this.ZoneBufferTopRight]= right / this.ZoneWidth;
        ZoneBuffer[this.ZoneBufferTopRight]= ZoneBuffer[this.ZoneBufferTopRight] +
                (top / this.ZoneHeight * ZonesX);

        ZoneBuffer[this.ZoneBufferBottomLeft]= left / this.ZoneWidth;
        ZoneBuffer[this.ZoneBufferBottomLeft]= ZoneBuffer[this.ZoneBufferBottomLeft] +
                (bottom / this.ZoneHeight * ZonesX);

        ZoneBuffer[this.ZoneBufferBottomRight]= right / this.ZoneWidth;
        ZoneBuffer[this.ZoneBufferBottomRight]= ZoneBuffer[this.ZoneBufferBottomRight] +
                (bottom / this.ZoneHeight * ZonesX);
    }

    private void updateZoneBuffers(SpriteExt sprite){
        if(sprite.getColBox().isRect())
            updateZoneBuffers((int)sprite.getColBox().getRect().left,
                    (int)sprite.getColBox().getRect().top,
                    (int)sprite.getColBox().getRect().right,
                    (int)sprite.getColBox().getRect().bottom);
        else if(sprite.getColBox().isCircle()){
            updateZoneBuffers((int)(sprite.getColBox().getCircle().x() - sprite.getColBox().getCircle().radius()),
                    (int)(sprite.getColBox().getCircle().y() - sprite.getColBox().getCircle().radius()),
                    (int)(sprite.getColBox().getCircle().x() + sprite.getColBox().getCircle().radius()),
                    (int)(sprite.getColBox().getCircle().y() + sprite.getColBox().getCircle().radius()));
        }
    }

    private void updateEnemyZone(Enemy e){
        updateZoneBuffers(e);
        for(int x=ZoneBuffer[this.ZoneBufferTopLeft]; x< ZoneBuffer[this.ZoneBufferTopRight]; x++){
            for(int y=0; y<((ZoneBuffer[this.ZoneBufferBottomLeft]-ZoneBuffer[this.ZoneBufferTopLeft])/ZonesX);y++){
                _Enemies.get(x+(y*ZonesX)).add(e);
            }
        }
    }

    private void updatePowerUpZone(PowerUp p){
        updateZoneBuffers(p);
        for(int x=ZoneBuffer[this.ZoneBufferTopLeft]; x< ZoneBuffer[this.ZoneBufferTopRight]; x++){
            for(int y=0; y<((ZoneBuffer[this.ZoneBufferBottomLeft]-ZoneBuffer[this.ZoneBufferTopLeft])/ZonesX);y++){
                _PowerUps.get(x+(y*ZonesX)).add(p);
            }
        }
    }

    private void updateProjectileZone(Projectile p, boolean friendly){
        updateZoneBuffers(p);
        for(int x=ZoneBuffer[this.ZoneBufferTopLeft]; x< ZoneBuffer[this.ZoneBufferTopRight]; x++){
            for(int y=0; y<((ZoneBuffer[this.ZoneBufferBottomLeft]-ZoneBuffer[this.ZoneBufferTopLeft])/ZonesX);y++){
                if(friendly) _FriendlyProjectiles.get(x+(y*ZonesX)).add(p);
                else _EnemyProjectiles.get(x+(y*ZonesX)).add(p);
            }
        }
    }

    private void updatePlayersZones(){
        updateZoneBuffers(player);
        PlayerZones.clear();
        for(int x=ZoneBuffer[this.ZoneBufferTopLeft]; x< ZoneBuffer[this.ZoneBufferTopRight]; x++){
            for(int y=0; y<((ZoneBuffer[this.ZoneBufferBottomLeft]-ZoneBuffer[this.ZoneBufferTopLeft])/ZonesX);y++){
                PlayerZones.add(x+(y*ZonesX));
            }
        }
    }

    private boolean Chance(int percent){
        return(rnd.nextInt(100)<=percent);
    }

    private void generatePowerup(float x,float y){
        if(Chance(20)){
            switch(rnd.nextInt(3)){
                case(0):
                    generatePoptartPowerUp(x,y);
                    break;
                case(1):
                    generateCrabPowerUp(x,y);
                    break;
                case(2):
                    generateHealthPowerUp(x,y);
                    break;
                case(3):
                    generateHealthPowerUp(x,y);
                    break;
            }
        }
    }

    private void generateCrabPowerUp(float x,float y){
        PowerUps.add(new CrabPowerUp(x,y,3 + (rnd.nextFloat() * 2),player));
    }

    private void generatePoptartPowerUp(float x,float y){
        PowerUps.add(new PoptartPowerUp(x,y,3 + (rnd.nextFloat() * 2),player));
    }

    private void generateHealthPowerUp(float x,float y){
        //90% chance of generating a pepsi
        if(Chance(90)) PowerUps.add(new PepsiPowerUp(x,y,3 + (rnd.nextFloat() * 2),player));
            //10% chance to generate an extra life
        else PowerUps.add(new LifePowerUp(x,y,3 + (rnd.nextFloat() * 2),player));
    }

    /**
     * Handle collisions for all objects.
     */
    private void _handleCollisions(){

        if(!playerdead){
            for(int i=0; i<PlayerZones.size();i++){

                //Player PowerUp Collisions
                for(int j=0; j < _PowerUps.get(i).size(); j++){
                    if(_PowerUps.get(i).get(j).isActive()){
                        if(collisionCheck(_PowerUps.get(i).get(j).getColBox(),player.getColBox())){
                            Audio_Manager.getInstance().playSound(pUpSound);
                            _PowerUps.get(i).get(j).AI(0);
                            PowerUpRemovalList.add(_PowerUps.get(i).get(j));
                            _PowerUps.get(i).remove(j);
                        }
                    }
                }

                if(!player.isInvincible()) {
                    //Check for collisions between player and enemies.
                    for (int j = 0; j < _Enemies.get(i).size(); j++) {
                        if(collisionCheck(_Enemies.get(i).get(j).getColBox(),player.getColBox())){
                            //If an enemy should die on collision
                            if(_Enemies.get(i).get(j).getDestroyOnCollide()) {
                                //Lazy death animation.
                                generateDeathParticles(_Enemies.get(i).get(j).getX()+(_Enemies.get(i).get(j).getFrame().width()*player.getScale())/2,
                                        _Enemies.get(i).get(j).getY()+(_Enemies.get(i).get(j).getFrame().height()*player.getScale())/2,20);
                                Audio_Manager.getInstance().playSound(explosion);

                                //Generate a power up
                                generatePowerup(_Enemies.get(i).get(j).getX()+(_Enemies.get(i).get(j).getFrame().width() * _Enemies.get(i).get(j).getScale())/2,
                                        _Enemies.get(i).get(j).getY()+(_Enemies.get(i).get(j).getFrame().height() * _Enemies.get(i).get(j).getScale())/2);

                                //Increment Score
                                if(score+_Enemies.get(i).get(j).getScoreValue()<Integer.MAX_VALUE)score+=_Enemies.get(i).get(j).getScoreValue();

                                //Remove the enemy
                                _Enemies.get(i).get(j).setActive(false);
                                EnemyRemovalList.add(_Enemies.get(i).get(j));
                                _Enemies.get(i).remove(j);
                            }
                            else{ //Otherwise
                                //Draw some collision particles.
                                generateCollisionParticles(_Enemies.get(i).get(j).getX(),_Enemies.get(i).get(j).getY()+(_Enemies.get(i).get(j).getFrame().width()*_Enemies.get(i).get(j).getScale())/2, 10);

                                //Decrement the enemies hp.
                                _Enemies.get(i).get(j).decHp(10);
                                //If the enemy died due to hp decrement.
                                if(!_Enemies.get(i).get(j).isActive()){
                                    Audio_Manager.getInstance().playSound(explosion);
                                    //implement death animation here.
                                    generateDeathParticles(_Enemies.get(i).get(j).getX()+(_Enemies.get(i).get(j).getFrame().width()*player.getScale())/2,
                                            _Enemies.get(i).get(j).getY()+(_Enemies.get(i).get(j).getFrame().height()*player.getScale())/2,20);

                                    //Generate powerups.
                                    generatePowerup(_Enemies.get(i).get(j).getX()+(_Enemies.get(i).get(j).getFrame().width() * _Enemies.get(i).get(j).getScale())/2,
                                            _Enemies.get(i).get(j).getY()+(_Enemies.get(i).get(j).getFrame().height() * _Enemies.get(i).get(j).getScale())/2);

                                    //Increment the Score.
                                    if(score+_Enemies.get(i).get(j).getScoreValue()<Integer.MAX_VALUE)score+=_Enemies.get(i).get(j).getScoreValue();

                                    //Remove the enemy.
                                    EnemyRemovalList.add(_Enemies.get(i).get(j));
                                    _Enemies.get(i).remove(j);

                                }
                            }
                            //subtract hp, and set invincibility
                            player.incHp(-15);
                            if(player.getHp()<=0){
                                Audio_Manager.getInstance().playSound(explosion);
                                player.setActive(false);
                                deathTimer=2.5f;
                                playerdead=true;

                                //Implement death animation here.
                                generateDeathParticles(player.getX()+(player.getFrame().width()*player.getScale())/2,
                                        player.getY()+(player.getFrame().height()*player.getScale())/2,20);
                                player.decLives();

                                //Subtract a life
                                if(player.getLives()<=0){
                                    GameOver=true;
                                    return;
                                }
                                player.resetHP();
                            }
                            else player.InitInvincibility(1);
                        }
                    }

                    //Enemy Projectile Player Collisions.
                    for (int j = 0; j < _EnemyProjectiles.get(i).size(); j++) {
                        if (_EnemyProjectiles.get(i).get(j).getActive()) {
                            if(collisionCheck(player.getColBox(),_EnemyProjectiles.get(i).get(j).getColBox())){
                                Audio_Manager.getInstance().playSound(hit3);
                                generateCollisionParticles_B(_EnemyProjectiles.get(i).get(j).getX(), _EnemyProjectiles.get(i).get(j).getY() + (_EnemyProjectiles.get(i).get(j).getFrame().width() * _EnemyProjectiles.get(i).get(j).getScale()) / 2, 3);
                                if(_EnemyProjectiles.get(i).get(j).getDestroyOnCollide()){
                                    _EnemyProjectiles.get(i).get(j).setActive(false);
                                    EnemyProjectileRemoval.add(_EnemyProjectiles.get(i).get(j));
                                    _EnemyProjectiles.get(i).remove(j);
                                }
                                player.incHp(-_EnemyProjectiles.get(i).get(j).getStrength());
                                if(player.getHp()<=0){
                                    Audio_Manager.getInstance().playSound(explosion);
                                    player.setActive(false);
                                    deathTimer=2.5f;
                                    playerdead=true;
                                    generateDeathParticles(player.getX()+(player.getFrame().width()*player.getScale())/2,player.getY()+(player.getFrame().height()*player.getScale())/2,20);
                                    player.decLives();
                                    if(player.getLives()<=0) {
                                        GameOver=true;
                                        return;
                                    }
                                    player.resetHP();
                                }
                                else player.InitInvincibility(1);
                            }

                        }
                    }
                }
            }
        }

        //Check for collisions between enemies and players projectiles.
        for(int i=0; i<_Enemies.size(); i++){
            for(int j=0; j < _Enemies.get(i).size();j++){
                for(int k=0; k < _FriendlyProjectiles.get(i).size(); k++){
                    if(_FriendlyProjectiles.get(i).get(k).getActive()){
                        //Check for collisions between enemies and projectiles
                        if(collisionCheck(_Enemies.get(i).get(j).getColBox(), _FriendlyProjectiles.get(i).get(k).getColBox())){
                            if(_FriendlyProjectiles.get(i).get(k).getDestroyOnCollide()){
                                //Play sound.
                                Audio_Manager.getInstance().playSound(hit1);
                                //Decrement Enemies Hp;
                                _Enemies.get(i).get(j).decHp(_FriendlyProjectiles.get(i).get(k).getStrength());
                                //Generate collision particles.
                                generateCollisionParticles_B(_FriendlyProjectiles.get(i).get(k).getX(),
                                        _FriendlyProjectiles.get(i).get(k).getY() + (_FriendlyProjectiles.get(i).get(k).getFrame().height() * _FriendlyProjectiles.get(i).get(k).getScale()) / 2, 3);

                                //Remove projectile.
                                _FriendlyProjectiles.get(i).get(k).setActive(false);
                                FriendlyProjectileRemoval.add(_FriendlyProjectiles.get(i).get(k));
                                _FriendlyProjectiles.remove(k);
                            }
                            else if(!_FriendlyProjectiles.get(i).get(k).getDestroyOnCollide() & _FriendlyProjectiles.get(i).get(k).isCollidable()) {
                                Audio_Manager.getInstance().playSound(hit1);
                                _Enemies.get(i).get(j).decHp(_FriendlyProjectiles.get(i).get(k).getStrength());
                                generateCollisionParticles_B(_FriendlyProjectiles.get(i).get(k).getX(),
                                        _FriendlyProjectiles.get(i).get(k).getY() + (_FriendlyProjectiles.get(i).get(k).getFrame().height() * _FriendlyProjectiles.get(i).get(k).getScale()) / 2, 3);
                                _FriendlyProjectiles.get(i).get(k).resetTimeToCollidable();
                            }

                            if(!_Enemies.get(i).get(j).isActive()){
                                Audio_Manager.getInstance().playSound(explosion);
                                generateDeathParticles(_Enemies.get(i).get(j).getX() + (_Enemies.get(i).get(j).getFrame().width() * _Enemies.get(i).get(j).getScale()) / 2,
                                        _Enemies.get(i).get(j).getY() + (_Enemies.get(i).get(j).getFrame().height() * _Enemies.get(i).get(j).getScale()) / 2, 20);
                                //Drop powerups here.
                                generatePowerup(_Enemies.get(i).get(j).getX() + (_Enemies.get(i).get(j).getFrame().width() * _Enemies.get(i).get(j).getScale()) / 2,
                                        _Enemies.get(i).get(j).getY() + (_Enemies.get(i).get(j).getFrame().height() * _Enemies.get(i).get(j).getScale()) / 2);
                                //Increment Score
                                if(score+_Enemies.get(i).get(j).getScoreValue()<Integer.MAX_VALUE) score+=_Enemies.get(i).get(j).getScoreValue();

                                //Remove Enemy.
                                EnemyRemovalList.add(_Enemies.get(i).get(j));
                                _Enemies.get(i).remove(j);
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleCollisions(){
        //PowerUp collisions
        if(!playerdead){
            for(int i=0; i<PowerUps.size();i++){
                if(PowerUps.get(i).isActive()){
                    if(collisionCheck(PowerUps.get(i).getColBox(),player.getColBox())){
                        Audio_Manager.getInstance().playSound(pUpSound);
                        PowerUps.get(i).AI(0);
                    }
                }
            }
        }

        //Enemy collisions
        for(Enemy enemy: Enemies){
            if(enemy.isActive()){
                //Check for collisions between enemy and players projectiles
                for(Projectile projectile: FriendlyProjectiles){
                    if(projectile.getActive() & projectile.isCollidable()){
                        //Check for collisions between enemies and projectiles
                        if(collisionCheck(enemy.getColBox(),projectile.getColBox())){
                            if(projectile.getDestroyOnCollide()){
                                FriendlyProjectileRemoval.add(projectile);
                                enemy.decHp(projectile.getStrength());
                                projectile.setActive(false);
                                Audio_Manager.getInstance().playSound(hit1);
                                generateCollisionParticles_B(projectile.getX(), projectile.getY() + (projectile.getFrame().height() * projectile.getScale()) / 2, 3);
                            }
                            else {
                                enemy.decHp(projectile.getStrength());
                                Audio_Manager.getInstance().playSound(hit1);
                                generateCollisionParticles_B(projectile.getX(), projectile.getY() + (projectile.getFrame().height() * projectile.getScale()) / 2, 3);
                                projectile.resetTimeToCollidable();
                            }

                            if(!enemy.isActive()){
                                Audio_Manager.getInstance().playSound(explosion);
                                generateDeathParticles(enemy.getX() + (enemy.getFrame().width() * enemy.getScale()) / 2, enemy.getY() + (enemy.getFrame().height() * enemy.getScale()) / 2, 20);
                                //Drop powerups here.
                                generatePowerup(enemy.getX() + (enemy.getFrame().width() * enemy.getScale()) / 2,
                                        enemy.getY() + (enemy.getFrame().height() * enemy.getScale()) / 2);

                                EnemyRemovalList.add(enemy);
                                if(score+enemy.getScoreValue()<Integer.MAX_VALUE) score+=enemy.getScoreValue();
                            }
                        }
                    }
                }
                CollisionEnemyandPlayer(enemy);
            }
        }

        //
        //Check for collisions between enemy projectiles and
        //
        if(!player.isInvincible() && !playerdead){
            for(Projectile projectile: EnemyProjectiles){
                if(projectile.getActive()){
                    //Check for collisions between player and enemy projectiles.
                    if(collisionCheck(player.getColBox(),projectile.getColBox())){
                        Audio_Manager.getInstance().playSound(hit3);
                        generateCollisionParticles_B(projectile.getX(), projectile.getY() + (projectile.getFrame().width() * projectile.getScale()) / 2, 3);
                        if(projectile.getDestroyOnCollide()){
                            projectile.setActive(false);
                            EnemyProjectileRemoval.add(projectile);
                        }
                        player.incHp(-projectile.getStrength());
                        if(player.getHp()<=0){
                            Audio_Manager.getInstance().playSound(explosion);
                            player.setActive(false);
                            deathTimer=2.5f;
                            playerdead=true;
                            generateDeathParticles(player.getX()+(player.getFrame().width()*player.getScale())/2,player.getY()+(player.getFrame().height()*player.getScale())/2,20);
                            player.decLives();
                            if(player.getLives()<=0) {
                                GameOver=true;
                                return;
                            }
                            player.resetHP();
                        }
                        else player.InitInvincibility(1);
                    }
                }
            }
        }
        //
        //
        //
    }

    //Generate collision particles between enemy and player.
    private void generateCollisionParticles(float x, float y, int amount){
        for(int i=0;i<amount; i++){
            ParticleList.add(new CollisionParticle(x,y, (MathF.PIOver2+ (rnd.nextFloat() * MathF.PI)),2));
        }
    }

    //Generate collision particles between player and enemy projeciles.
    private void generateCollisionParticles_B(float x, float y, int amount){
        for(int i=0;i<amount; i++){
            ParticleList.add(new CollisionParticle(x,y, (rnd.nextFloat() * MathF.Pi2),2));
        }
    }

    //Death animation.
    private void generateDeathParticles(float x, float y, int amount){
        for(int i=0;i<amount; i++){
            ParticleList.add(new ExplosionParticle(x,y, (rnd.nextFloat() * MathF.Pi2) ,(int)(rnd.nextFloat() * 5)));
        }
    }

    private void CollisionEnemyandPlayer(Enemy enemy){
        //Check for collisions between player and enemies.
        if(!player.isInvincible() && !playerdead){
            //Check for collisions between player and enemies.
            if(collisionCheck(enemy.getColBox(),player.getColBox())){
                //If an enemy should die on collision
                if(enemy.getDestroyOnCollide()) {
                    //Lazy death animation.
                    generateDeathParticles(enemy.getX()+(enemy.getFrame().width()*player.getScale())/2,
                            enemy.getY()+(enemy.getFrame().height()*player.getScale())/2,20);
                    Audio_Manager.getInstance().playSound(explosion);
                    //Generate a power up
                    generatePowerup(enemy.getX()+(enemy.getFrame().width() * enemy.getScale())/2,
                            enemy.getY()+(enemy.getFrame().height() * enemy.getScale())/2);
                    enemy.setActive(false);
                    EnemyRemovalList.add(enemy);
                    if(score+enemy.getScoreValue()<Integer.MAX_VALUE)score+=enemy.getScoreValue();
                }
                //Otherwise
                else{
                    //Draw some collision particles.
                    generateCollisionParticles( enemy.getX(), enemy.getY()+(enemy.getFrame().width()*enemy.getScale())/2, 10);

                    //Decrement the enemies hp.
                    enemy.decHp(10);
                    //If the enemy died to hp decrement.
                    if(!enemy.isActive()){
                        Audio_Manager.getInstance().playSound(explosion);
                        //implement death animation here.
                        generateDeathParticles(enemy.getX()+(enemy.getFrame().width()*player.getScale())/2,
                                enemy.getY()+(enemy.getFrame().height()*player.getScale())/2,20);

                        //Generate powerups.
                        generatePowerup(enemy.getX()+(enemy.getFrame().width() * enemy.getScale())/2,
                                enemy.getY()+(enemy.getFrame().height() * enemy.getScale())/2);
                        EnemyRemovalList.add(enemy);
                        if(score+enemy.getScoreValue()<Integer.MAX_VALUE)score+=enemy.getScoreValue();
                    }
                }
                //subtract hp, and set invincibility
                player.incHp(-15);
                if(player.getHp()<=0){
                    Audio_Manager.getInstance().playSound(explosion);
                    player.setActive(false);
                    deathTimer=2.5f;
                    playerdead=true;
                    generateDeathParticles(player.getX()+(player.getFrame().width()*player.getScale())/2,
                            player.getY()+(player.getFrame().height()*player.getScale())/2,20);
                    player.decLives();
                    //Implement death animation here.
                    //Subtract a life
                    if(player.getLives()<=0){
                        GameOver=true;
                        return;
                    }
                    player.resetHP();
                }
                else player.InitInvincibility(1);
            }
        }
    }

    private boolean collisionCheck(ShapeF a, ShapeF b){
        if (a.isCircle() & b.isCircle()) return Collisions.Check(a.getCircle(),b.getCircle());
        else if(a.isCircle() & b.isRect()) return Collisions.Check(a.getCircle(),b.getRect());
        else if (a.isRect() & b.isCircle()) return Collisions.Check(a.getRect(), b.getCircle());
        else if (a.isRect() & b.isRect()) return Collisions.Check(a.getRect(),b.getRect());
        return false;
    }

    private void onResume(){
        try{
            File file=new File(Environment.getExternalStorageDirectory(), "save.sve");
            if(file.exists()){
                Scanner reader=new Scanner(file);
                String sIn;
                while (reader.hasNextLine()){
                    sIn=reader.nextLine();
                    if(sIn.equals(ObjSerializer.oScore)) this.score=Integer.parseInt(reader.nextLine());
                    else if (sIn.equals(ObjSerializer.oNextUpdate)) this.nextUpdate=Float.parseFloat(reader.nextLine());
                    else if (sIn.equals(ObjSerializer.oPlayer)) player=gson.fromJson(reader.nextLine(),Player.class);
                    else if (sIn.equals(ObjSerializer.oRobot)) Enemies.add(gson.fromJson(reader.nextLine(),Robot.class));
                    else if (sIn.equals(ObjSerializer.oSpaceCrab)) {
                        SpaceCrab spaceCrab=gson.fromJson(reader.nextLine(),SpaceCrab.class);
                        spaceCrab.init_onLoad(getPlayer());
                        Enemies.add(spaceCrab);
                    }
                    else if (sIn.equals(ObjSerializer.oSpaceSubmarine)) Enemies.add(gson.fromJson(reader.nextLine(),SpaceSubmarine.class));
                    else if (sIn.equals(ObjSerializer.oGarbageTruck)) Enemies.add(gson.fromJson(reader.nextLine(),GarbageTruck.class));
                    else if (sIn.equals(ObjSerializer.oEnemyProjectile)) EnemyProjectiles.add(gson.fromJson(reader.nextLine(),Projectile.class));
                    else if (sIn.equals(ObjSerializer.oFriendlyProjectile)) FriendlyProjectiles.add(gson.fromJson(reader.nextLine(),Projectile.class));
                    else if (sIn.equals(ObjSerializer.oCollisionParticle)) ParticleList.add(gson.fromJson(reader.nextLine(),CollisionParticle.class));
                    else if (sIn.equals(ObjSerializer.oExplosionParticle)) ParticleList.add(gson.fromJson(reader.nextLine(),ExplosionParticle.class));
                    else if (sIn.equals(ObjSerializer.oPepsiPowerUp)) {
                        PepsiPowerUp pUp=gson.fromJson(reader.nextLine(),PepsiPowerUp.class);
                        pUp.init_onLoad(getPlayer());
                        PowerUps.add(pUp);
                    }
                    else if (sIn.equals(ObjSerializer.oLifePowerUp)) {
                        LifePowerUp pUp=gson.fromJson(reader.nextLine(),LifePowerUp.class);
                        pUp.init_onLoad(getPlayer());
                        PowerUps.add(pUp);
                    }
                    else if (sIn.equals(ObjSerializer.oPoptartPowerUp)) {
                        PoptartPowerUp pUp=gson.fromJson(reader.nextLine(),PoptartPowerUp.class);
                        pUp.init_onLoad(getPlayer());
                        PowerUps.add(pUp);
                    }
                    else if (sIn.equals(ObjSerializer.oCrabPowerUp)) {
                        CrabPowerUp pUp=gson.fromJson(reader.nextLine(),CrabPowerUp.class);
                        pUp.init_onLoad(getPlayer());
                        PowerUps.add(pUp);
                    }
                }
                reader.close();
            }
        }
        catch (Exception e){ e.printStackTrace(); }

    }
    public void onPause(){
        try{
            if(!GameOver) {
                File file=new File(Environment.getExternalStorageDirectory(), "save.sve");
                FileOutputStream Fout=new FileOutputStream(file);
                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(Fout));

                writer.write(ObjSerializer.oScore);
                writer.newLine();
                writer.write(""+ this.getScore());
                writer.newLine();

                writer.write(ObjSerializer.oNextUpdate);
                writer.newLine();
                if(this.timer<=this.nextUpdate) writer.write(""+(this.nextUpdate-this.timer));
                else writer.write(""+0.00f);
                writer.newLine();

                writer.write(ObjSerializer.oPlayer);
                writer.newLine();
                writer.write(gson.toJson(player));

                for (Projectile FriendlyProj : FriendlyProjectiles) {
                    if (FriendlyProj.getActive()) {
                        writer.newLine();
                        writer.write(ObjSerializer.oFriendlyProjectile);
                        writer.newLine();
                        writer.write(gson.toJson(FriendlyProj));
                    }
                }

                for (Enemy enemy : Enemies) {
                    if (enemy.isActive()) {
                        writer.newLine();
                        if(enemy instanceof Robot) writer.write(ObjSerializer.oRobot);
                        else if (enemy instanceof SpaceCrab) writer.write(ObjSerializer.oSpaceCrab);
                        else if (enemy instanceof SpaceSubmarine) writer.write(ObjSerializer.oSpaceSubmarine);
                        else if (enemy instanceof GarbageTruck) writer.write(ObjSerializer.oGarbageTruck);
                        writer.newLine();
                        writer.write(gson.toJson(enemy));
                    }
                }
                for (Projectile enemyProj : EnemyProjectiles) {
                    if (enemyProj.getActive()) {
                        writer.newLine();
                        writer.write(ObjSerializer.oEnemyProjectile);
                        writer.newLine();
                        writer.write(gson.toJson(enemyProj));
                    }
                }


                for (Particle particle : ParticleList) {
                    if (particle.isActive()) {
                        writer.newLine();
                        if(particle instanceof CollisionParticle) writer.write(ObjSerializer.oCollisionParticle);
                        else if(particle instanceof ExplosionParticle) writer.write(ObjSerializer.oExplosionParticle);
                        writer.newLine();
                        writer.write(gson.toJson(particle));
                    }
                }

                for (PowerUp powerUp : PowerUps) {
                    if (powerUp.isActive()) {
                        writer.newLine();
                        if(powerUp instanceof PepsiPowerUp) writer.write(ObjSerializer.oPepsiPowerUp);
                        else if(powerUp instanceof LifePowerUp) writer.write(ObjSerializer.oLifePowerUp);
                        else if (powerUp instanceof PoptartPowerUp) writer.write(ObjSerializer.oPoptartPowerUp);
                        else if (powerUp instanceof CrabPowerUp) writer.write(ObjSerializer.oCrabPowerUp);
                        writer.newLine();
                        writer.write(gson.toJson(powerUp));
                    }
                }
                writer.close();

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean getGameOver(){ return GameOver;}
    public void Reset(){
        //Active item lists.
        Enemies= new ArrayList<Enemy>();
        FriendlyProjectiles= new ArrayList<Projectile>();
        EnemyProjectiles= new ArrayList<Projectile>();
        ParticleList=new ArrayList<Particle>();
        PowerUps=new ArrayList<PowerUp>();

        //Removal lists.
        EnemyRemovalList= new HashSet<Enemy>();
        FriendlyProjectileRemoval=new HashSet<Projectile>();
        EnemyProjectileRemoval=new HashSet<Projectile>();
        ParticleRemovalList=new HashSet<Particle>();
        PowerUpRemovalList=new HashSet<PowerUp>();

        Audio_Manager.getInstance().unloadAtRuntime();
        player=new Player(100);
        timer=0;
        deathTimer=0;
        nextUpdate=0;
        score=0;
        GameOver=false;
    }
}
