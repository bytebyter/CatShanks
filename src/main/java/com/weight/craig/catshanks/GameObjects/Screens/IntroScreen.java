package com.weight.craig.catshanks.GameObjects.Screens;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;

import com.weight.craig.catshanks.Collision.Collisions;
import com.weight.craig.catshanks.ViewModels.GameModel;
import com.weight.craig.catshanks.GameObjects.Players.Player;
import com.weight.craig.catshanks.R;
import com.weight.craig.catshanks.Support.Resolution;
import com.weight.craig.catshanks.BaseObjects.Screen;
import com.weight.craig.catshanks.BaseObjects.Weapon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;


/**
 * Created by Craig on 11/27/13.
 */
public class IntroScreen implements Screen{
    private StringBuilder stringBuilder=new StringBuilder(6);
    private GameModel gameModel=new GameModel();
    private Random rnd=new Random(System.currentTimeMillis());

    private final int Mode_IntroA=0;
    private final int Mode_IntroB=1;
    private final int Mode_Game=4;
    private final int Mode_HighScores=5;
    private int Mode=Mode_IntroA;

    private int ScreenWidth, ScreenHeight;
    private float ScaleSprites=1;

    private int IntroTexture,TsheetA,TsheetB;
    private float timer=0.0f;

    private float Scale=0;
    private float offset=0;
    private float IntroX,IntroY;
    private float IntroWidth,IntroHeight;
    private float joyX,joyY;
    private float playerX,playerY;

    private HashMap<Integer,PointF> TouchDownAt=new HashMap<Integer,PointF>();
    private int PointerIndex, PointerId;


    private PointF [] planets= new PointF[3];
    private PointF [] stars= new PointF [20];
    private int [] starsT=new int[20];
    private RectF [] starsR={
            new RectF(768,0,896,128),
            new RectF(896,0,960,64),
            new RectF(896,64,960,128),
            new RectF(960,0,1024,64),
    };
    private RectF [] planetsR={
            new RectF(0,0,256,256),
            new RectF(256,0,512,256),
            new RectF(512,0,768,256),
    };
    private RectF [] IntroR={
            new RectF(210,2,810,512),
            new RectF(0,512,1024,1024)
    };
    private RectF[] HealthBarR={
            new RectF(0,0,766,128),
            new RectF(768,0,896,128)
    };
    private RectF[] NumbersR={
            new RectF(898,192,960,256),     //0
            new RectF(960,192,1024,256),    //1
            new RectF(512,256,576,320),     //2
            new RectF(576,256,640,320),     //3
            new RectF(640,256,704,320),     //4
            new RectF(704,256,768,320),     //5
            new RectF(768,256,832,320),     //6
            new RectF(832,256,896,320),     //7
            new RectF(896,256,960,320),     //8
            new RectF(960,256,1024,320)     //9
    };

    private RectF[] AlphabetR={
            new RectF (512,320,576,384),    //a
            new RectF (576,320,640,384),    //b
            new RectF (640,320,704,384),    //c
            new RectF (704,320,768,384),    //d
            new RectF (768,320,832,384),    //e
            new RectF (832,320,896,384),    //f
            new RectF (896,320,960,384),    //g
            new RectF (960,320,1024,384),   //h
            new RectF (512,384,576,448),    //i
            new RectF (576,384,640,448),    //j
            new RectF (640,384,704,448),    //k
            new RectF (704,384,768,448),    //l
            new RectF (768,384,832,448),    //m
            new RectF (832,384,896,448),    //n
            new RectF (896,384,960,448),    //o
            new RectF (960,384,1024,448),   //p
            new RectF (512,448,576,512),    //q
            new RectF (576,448,640,512),    //r
            new RectF (640,448,704,512),    //s
            new RectF (704,448,768,512),    //t
            new RectF (768,448,832,512),    //u
            new RectF (832,448,896,512),    //v
            new RectF (896,448,960,512),    //w
            new RectF (960,448,1024,512),   //x
            new RectF (768,512,832,576),    //y
            new RectF (832,512,896,576)     //z
    };

    private RectF[] ControlsTR={
            new RectF(768,640,896,768),     // Life
            new RectF(768,768,1024,1024),   // Weapon Slider
            new RectF (512,768,768,1024),   //Control Stick
            new RectF (768,768,1024,1024)   //Action Button
    };

    private RectF[] WeaponsR={
            new RectF(0,256,128,384),	    //HairBall
            new RectF(512,640,640,768),	    //Rainbow Laser
            new RectF(640,640,768,768)	    //Crab-Claw
    };

    private TreeSet<Integer> Scores=new TreeSet<Integer>();

    private final String HighScoreText="HI SCORES";
    private RectF Joystick;

    private RectF WeaponSlider;
    private Player player;

    private int ChangeSelection,ChangeSelection2;
    public IntroScreen(Context context){
        //Load the necessary sounds and textures here.
        TsheetA=textureManager.addTexture(R.drawable.tilesheet1);
        IntroTexture=textureManager.addTexture(R.drawable.intro);
        TsheetB=textureManager.addTexture(R.drawable.tilesheet2);
        ChangeSelection=audioManager.addSound("change.wav");
        ChangeSelection2=audioManager.addSound("change2.wav");

        //Load previous scores here.
        loadScores();
    }

    private void loadScores(){
        Scores=new TreeSet<Integer>();
        int totalScores;
        try{
            File file=new File(Environment.getExternalStorageDirectory(), "catshank_scores.sve");
            if(file.exists()){
                FileInputStream Fin=new FileInputStream(file);
                BufferedReader reader=new BufferedReader(new InputStreamReader(Fin));
                totalScores=Integer.parseInt(reader.readLine());
                for(int i=0; i<totalScores;i++){
                    Scores.add(Integer.parseInt(reader.readLine()));
                }
                Fin.close();
            }
        }
        catch (Exception e){
            Log.e("Loading Scores Error:", e.getLocalizedMessage());
        }
    }

    private void saveScores(){
        int scoreCount=0;
        try{
            File file=new File(Environment.getExternalStorageDirectory(), "catshank_scores.sve");
            FileOutputStream Fout=new FileOutputStream(file);
            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(Fout));

            if(Scores.size()<=5) writer.write(""+Scores.size());
            else writer.write(""+5);
            writer.newLine();
            for(int score:Scores.descendingSet()){
                if(scoreCount<5) {
                    writer.write(""+ score);
                    writer.newLine();
                }
                else break;
                scoreCount++;
            }

            writer.close();
            Fout.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setScreenSize(int width, int height) {
        this.ScreenWidth=width;
        this.ScreenHeight=height;
        ScaleSprites=((float)(width*.50)/768);//768 is the largest sprites width;
        Initialize_IntroScreen(width,height);
        Joystick=new RectF(0,(float)height-((float)height * 0.5f),(float)width * 0.5f,(float)height);

        //Batcher.Draw(TsheetA,ControlsTR[1],ScreenWidth *0.80f,0,(0.20f * ((float)ScreenWidth/ControlsTR[1].width())), (0.25f * ((float)ScreenHeight/ControlsTR[1].height())));
        WeaponSlider=new RectF(ScreenWidth *0.80f,0,ScreenWidth,ScreenHeight * 0.25f);

        if(player!=null) player.setScale(ScaleSprites);

    }

    @Override
    public void onResume() {
        //Do nothing here, as this screen doesn't require anything to be saved.
    }




    @Override
    public void update(float deltaTime) {
        timer+=deltaTime;

        //Start sprite batching here.
        Batcher.begin();
        switch(Mode){
            case(Mode_IntroA):{
                Intro_Mode(deltaTime);
                break;
            }
            case(Mode_IntroB):{
                Intro_ModeB();
                break;
            }
            case(Mode_HighScores):
                if(timer>10){
                    timer=0;
                    gameModel.Reset();
                    ChangeSelection=audioManager.addSound("change.wav");
                    ChangeSelection2=audioManager.addSound("change2.wav");
                    joyX=0;
                    joyY=0;
                    TouchDownAt.clear();
                    Mode=Mode_IntroA;
                    return;
                }
                int position=0;
                drawStarfield();
                drawCenteredText(HighScoreText,0);
                if(Scores.size()>=5){
                    for(int score:Scores.descendingSet()){
                        if (position<5) drawScore(score,((position+1) * 64));
                        else break;
                        position++;
                    }
                }
                else{
                    for(int score:Scores.descendingSet()){
                        drawScore(score,((position+1) * 64));
                        position++;
                    }
                }
                break;
            case(Mode_Game):{
                if(gameModel.getGameOver()){
                    File file=new File(Environment.getExternalStorageDirectory(), "save.sve");
                    if (file.exists()) file.delete();
                    Mode=Mode_HighScores;
                    Scores.add(gameModel.getScore());
                    saveScores();
                    timer=0;
                }

                drawStarfield();

                playerX=Math.max(player.getX()+(2*joyX),
                        -(player.getFrame().width()*player.getScale())/2);
                //playerX=Math.min(playerX,Resolution.getInstance().getWidth()+ ((player.getFrame().width() * player.getScale())/2));
                if(playerX+(player.getFrame().width()*player.getScale())/2 > Resolution.getInstance().getWidth())
                    playerX=Resolution.getInstance().getWidth()-(player.getFrame().width()*player.getScale())/2;

                playerY=Math.max(player.getY()-(2*joyY),
                        -(player.getFrame().height()*player.getScale())/2);
                if(playerY+(player.getFrame().height()*player.getScale())/2 > Resolution.getInstance().getHeight())
                    playerY=Resolution.getInstance().getHeight()-(player.getFrame().height()*player.getScale())/2;

                //playerY=Math.min(playerY,
                //        Resolution.getInstance().getHeight() + ((player.getFrame().height() * player.getScale())/2));


                //player.setPos(player.getX()+(2*joyX),player.getY()-(2* joyY));
                player.setPos(playerX,playerY);

                gameModel.Update(deltaTime);

                //Draw player
                if(player.isActive()) Batcher.Draw(player.getTexture(),player.getFrame(), player.getX(),player.getY(),ScaleSprites);

                //Draw Enemies
                for(int i=0;i<gameModel.getEnemies().size();i++){
                    //gameModel.getEnemies().get(i).Update(deltaTime);
                    if(gameModel.getEnemies().get(i).isActive()){
                        Batcher.Draw(gameModel.getEnemies().get(i).getTexture(),gameModel.getEnemies().get(i).getFrame(),
                                gameModel.getEnemies().get(i).getX(),gameModel.getEnemies().get(i).getY(),ScaleSprites);
                    }
                }

                //Draw Enemy Projectiles
                for(int i=0;i<gameModel.getEnemyProjectiles().size();i++){
                    if(gameModel.getEnemyProjectiles().get(i).getActive()){
                        Batcher.Draw(gameModel.getEnemyProjectiles().get(i).getTexture(),gameModel.getEnemyProjectiles().get(i).getFrame(),
                                gameModel.getEnemyProjectiles().get(i).getX(),gameModel.getEnemyProjectiles().get(i).getY(),ScaleSprites);
                    }
                }

                //Draw Friendly Projectiles
                for(int i=0;i<gameModel.getFriendlyProjectiles().size();i++){
                    if(gameModel.getFriendlyProjectiles().get(i).getActive()){
                        Batcher.Draw(gameModel.getFriendlyProjectiles().get(i).getTexture(),gameModel.getFriendlyProjectiles().get(i).getFrame(),
                                gameModel.getFriendlyProjectiles().get(i).getX(),gameModel.getFriendlyProjectiles().get(i).getY(),ScaleSprites);
                    }
                }

                //Draw Particles.
                for(int i=0;i<gameModel.getParticleList().size(); i++){
                    if(gameModel.getParticleList().get(i).isActive()){
                        Batcher.Draw(gameModel.getParticleList().get(i).getTexture(),gameModel.getParticleList().get(i).getFrame(),
                                gameModel.getParticleList().get(i).getX(),gameModel.getParticleList().get(i).getY(),ScaleSprites);
                    }
                }

                //Draw Powerups
                for(int i=0;i<gameModel.getPowerUps().size(); i++){
                    if(gameModel.getPowerUps().get(i).isActive()){
                        Batcher.Draw(gameModel.getPowerUps().get(i).getTexture(),gameModel.getPowerUps().get(i).getFrame(),
                                gameModel.getPowerUps().get(i).getX(),gameModel.getPowerUps().get(i).getY(),ScaleSprites);
                    }
                }

                drawScore(gameModel.getScore());

                if(player.GetWeapon()==Weapon.HairBall) drawWeaponSlider(0);
                else if (player.GetWeapon()==Weapon.Rainbow) drawWeaponSlider(1);
                else drawWeaponSlider(2);

                //Draw onscreen controls/etc..
                drawHealthBar((float)player.getHp()/(float)player.getMaxHP());
                drawLives(player.getLives());
                drawVirtualControls(joyX,joyY);

                break;
            }
        }
        //End sprite batching.
        Batcher.end();
    }


    @Override
    public Screen getScreen() { return this; }

    @Override
    public void onTouch(MotionEvent event) {
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_POINTER_DOWN:                       //Check for subsequent Pointer down events.
            case MotionEvent.ACTION_DOWN:{                              //Check for first finger down.
                PointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                PointerId= event.getPointerId(PointerIndex);

                if(TouchDownAt.containsKey(PointerId)) TouchDownAt.get(PointerId).set(event.getX(event.findPointerIndex(PointerId)),event.getY(event.findPointerIndex(PointerId)));
                else TouchDownAt.put(PointerId,new PointF(event.getX(event.findPointerIndex(PointerId)),event.getY(event.findPointerIndex(PointerId))));

                ProcessTouchDown(event.getX(event.findPointerIndex(PointerId)),event.getY(event.findPointerIndex(PointerId)));
                //Log.i("TouchDown:",""+PointerId);
                break;
            }
            case MotionEvent.ACTION_MOVE:{                              //A finger move event has occurred.
                for(int i=0;i<event.getPointerCount();i++){
                    PointerId= event.getPointerId(i);
                    ProcessTouchMove(PointerId,event.getX(event.findPointerIndex(PointerId)),event.getY(event.findPointerIndex(PointerId)));
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:                         //A finger has been lifted.
            case MotionEvent.ACTION_UP:{
                PointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                PointerId= event.getPointerId(PointerIndex);
                ProcessTouchUp(PointerId,event.getX(event.findPointerIndex(PointerId)),event.getY(event.findPointerIndex(PointerId)));
                break;
            }
        }

    }


    //
    //Touch Down Events.
    //
    private void ProcessTouchDown(float x, float y){
        switch(Mode){
            case(Mode_IntroA):
            case(Mode_IntroB):
                timer=0;
                //audioManager.LoadMusic("2.mp3");
                gameModel.Initialize();
                player=gameModel.getPlayer();
                Mode=Mode_Game;
                player.setY((ScreenHeight-player.getFrame().height())/2);
                break;
            case(Mode_Game):

                break;
            case(Mode_HighScores):
                if(timer>1){
                    timer=0;
                    gameModel.Reset();
                    ChangeSelection=audioManager.addSound("change.wav");
                    ChangeSelection2=audioManager.addSound("change2.wav");
                    joyX=0;
                    joyY=0;
                    TouchDownAt.clear();
                    Mode=Mode_IntroA;
                }
                break;
        }
    }

    //
    //Touch move events.
    //
    private void ProcessTouchMove(int PointerId, float x, float y){
        switch (Mode){
            case(Mode_Game):{
                //Handle Joystick events.
                if(Joystick!=null && (Collisions.isPointInRect(TouchDownAt.get(PointerId),Joystick))){
                    joyX=(float) Math.sqrt((x-((float)ScreenWidth*0.20)) * (x-((float)ScreenWidth*0.20)))/((float)ScreenWidth*0.10f);
                    if(x<((float)ScreenWidth*0.20)) joyX=-joyX;

                    joyY=(float) Math.sqrt((y-((float)ScreenHeight*0.75)) * (y-((float)ScreenHeight*0.75)))/((float)ScreenHeight*0.10f);
                    if(y>((float)ScreenHeight*0.75)) joyY=-joyY;
                }
                break;
            }
        }
    }

    //
    //Process touch up events.
    //
    private void ProcessTouchUp(int PointerId, float x, float y){
        switch (Mode){
            case(Mode_Game):{
                //Handle Joystick events.
                if(Joystick!=null & (Collisions.isPointInRect(TouchDownAt.get(PointerId),Joystick))){
                    joyX=0;
                    joyY=0;
                    TouchDownAt.get(PointerId).set(-1, -1);
                }

                else if (WeaponSlider!=null & Collisions.isPointInRect(TouchDownAt.get(PointerId),WeaponSlider)){
                    if(x-WeaponSlider.centerX()>WeaponSlider.width()/5 & y - WeaponSlider.centerY()  < -WeaponSlider.height()/5){
                        player.incWeapon();
                        audioManager.playSound(ChangeSelection);
                    }
                    else if (x - WeaponSlider.centerX() < -WeaponSlider.width()/5 & y - WeaponSlider.centerY() > WeaponSlider.height()/5){
                        player.decWeapon();
                        audioManager.playSound(ChangeSelection2);
                    }
                    TouchDownAt.get(PointerId).set(-1,-1);
                }
                break;
            }
        }
    }

    @Override
    public void onPause() {
        //For some reason calling release on the mediaplayer object causes crashing,
        //hence the only thing I can do is stop playing the music.
        //audioManager.unloadAtRuntime();
        audioManager.unload();
        if(Mode==Mode_Game){gameModel.onPause();}
    }

    //
    //Intro stuff
    //
    private void Initialize_IntroScreen(int width, int height){
        float T1=Math.min(width,height);
        float T2=Math.max(IntroR[0].width() , IntroR[0].height());
        Scale=Math.min(T2/T1,T1/T2);
        IntroWidth=(IntroR[0].width() * Scale);
        IntroHeight=(IntroR[0].height()) * Scale;
        IntroX=((float)width-IntroWidth)/2;
        IntroY=((float)height-IntroHeight);
        randStarfield();
    }

    private void randStarfield(){

        for(int i=0;i<stars.length;i++){
            stars[i]=new PointF();
            starsT[i]=(rnd.nextInt(starsR.length-1));
            stars[i].x=(rnd.nextInt(ScreenWidth));
            stars[i].y=(rnd.nextInt(ScreenHeight));
        }
    }

    private void drawStarfield(){
        for(int x=0;x<=(ScreenWidth/128)+1;x++){
            for(int y=0; y<=(ScreenHeight/128);y++){
                Batcher.Draw(TsheetA,768,128,896,256,(x*128)-offset,y*128);
            }
        }
        offset+=2;
        if(offset>128) offset=offset-128;
        for(int i=0;i<stars.length;i++){
            stars[i].x-=2;
            if(stars[i].x <-128){
                stars[i].x=ScreenWidth;
                stars[i].y=rnd.nextFloat() * ScreenHeight;
            }
            Batcher.Draw(TsheetA,starsR[starsT[i]].left,starsR[starsT[i]].top,starsR[starsT[i]].right,starsR[starsT[i]].bottom,(int)stars[i].x,(int)stars[i].y);
        }
    }

    private void Intro_Mode(float deltaTime){
        if (timer>=30){
            Mode=Mode_IntroB;
            timer=0;
            return;
        }
        drawStarfield();
        Batcher.Draw(IntroTexture,IntroR[0].left,IntroR[0].top,IntroR[0].right,IntroR[0].bottom,IntroX,IntroY,IntroWidth,IntroHeight);
        drawCenteredText("CATSHANKS",0);
    }

    private void Intro_ModeB(){
        if(timer>=3){
            Mode=Mode_IntroA;
            timer=0;
        }
        Batcher.Draw(IntroTexture,IntroR[1].left,IntroR[1].top,IntroR[1].right,IntroR[1].bottom,0,0,ScreenWidth,ScreenHeight);
    }

    //
    //Stuff for drawing the interface...
    //Hardcoded, as I have given up on doing this the right way.
    //
    private void drawVirtualControls(float x, float y){
        float scaleHeight=0.15f * (ScreenHeight/ControlsTR[3].height());

        x=Math.max(-1,x);
        x=Math.min(1,x);
        y=Math.max(-1,y);
        y=Math.min(1,y);

        x=(1f + x)/2f;
        y=(1f + y)/2f;

        Batcher.Draw(TsheetB, ControlsTR[2],(x/2*(ScreenWidth*0.30f)),(ScreenHeight-((ScreenHeight*0.30f)*y) - (ControlsTR[3].height() * scaleHeight)),(0.10f * (ScreenWidth/ControlsTR[2].width())), scaleHeight);
        //Batcher.Draw(TsheetB,ControlsTR[3],(ScreenWidth-(ScreenWidth *0.20f)),(ScreenHeight-(ScreenHeight * 0.27f)),0.15f*(ScreenWidth/ControlsTR[3].width()), 0.25f*(ScreenHeight/ControlsTR[3].height()));
    }

    private void drawScore(int Score){
        stringBuilder.replace(0,6,String.valueOf(Score));
        int width=stringBuilder.length()*64;
        for(int i=0; i<stringBuilder.length();i++){
            Batcher.Draw(TsheetA,NumbersR[ Integer.parseInt(""+stringBuilder.charAt(i))],((ScreenWidth-width)*0.50f)+(i*64),(float)ScreenHeight -64f,1f);
        }
    }

    private void drawScore(int Score,float y){
        stringBuilder.replace(0,6,String.valueOf(Score));
        int width=stringBuilder.length()*64;
        for(int i=0; i<stringBuilder.length();i++){
            Batcher.Draw(TsheetA,NumbersR[ Integer.parseInt(""+stringBuilder.charAt(i))],((ScreenWidth-width)*0.50f)+(i*64),y,1f);
        }
    }


    private void drawHealthBar(float percent){
        Batcher.Draw(TsheetB,HealthBarR[1],(0.01f*ScreenWidth),(0.02f*ScreenHeight),
                (0.38f * percent)*((float)ScreenWidth/HealthBarR[1].width()),
                0.11f*((float)ScreenHeight/HealthBarR[1].height()));
        Batcher.Draw(TsheetB,HealthBarR[0],0,0,(float)(ScreenWidth*.40)/HealthBarR[0].width(),(float)(ScreenHeight*.15)/HealthBarR[0].height());
    }

    private void drawLives(int Lives){
        for(int i=0; i<Lives; i++){
            Batcher.Draw(TsheetB,ControlsTR[0],((64f*i) + 32), ScreenHeight *0.16f, 0.5f);
        }
    }

    private void drawWeaponSlider(int Weapon){
        Batcher.Draw(TsheetA,ControlsTR[1],ScreenWidth *0.80f,0,(0.20f * ((float)ScreenWidth/ControlsTR[1].width())), (0.25f * ((float)ScreenHeight/ControlsTR[1].height())));
        if(!(Weapon<0) && !(Weapon>WeaponsR.length)){
            Batcher.Draw(TsheetB,WeaponsR[Weapon],ScreenWidth *0.85f,ScreenHeight*0.05f,(0.10f * ((float)ScreenWidth/WeaponsR[Weapon].width())), (0.15f * ((float)ScreenHeight/WeaponsR[Weapon].height())));
        }
    }

    private void drawCenteredText(String drawMe,float y){
        float posX=0; int charVal;
        float CalculatedWidth=drawMe.length()*64;
        float startX=(Resolution.getInstance().getWidth()-CalculatedWidth)/2;

        for(int i=0;i<drawMe.length();i++){
            if(drawMe.charAt(i)==' '){
                posX+=64;
            }
            else if(drawMe.charAt(i)=='\n'){
                drawCenteredText(drawMe.substring(i,drawMe.length()-i), y+64);
            }
            else{
                charVal=(int)drawMe.charAt(i)-65;
                if(charVal>=0 & charVal<=AlphabetR.length){
                    Batcher.Draw(TsheetA,AlphabetR[charVal],startX+posX,y,1);
                }
                posX+=64;
            }
        }
    }

}
