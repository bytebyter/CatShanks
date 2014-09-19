package com.weight.craig.catshanks;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.util.ArrayList;

/**
 * Created by Craig on 11/27/13.
 */
public class Audio_Manager implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener {
    private static Audio_Manager instance;
    private boolean isPrepared=false;
    private AssetManager assets;
    private ArrayList<Sound> SoundList=new ArrayList<Sound>();
    private SoundPool Sounds=new SoundPool(15, AudioManager.STREAM_MUSIC,0);
    private MediaPlayer Music;
    private Context context;

    private float GameVolume=0.5f;


    private class Sound{
        private String filename;
        private int Id;
        public Sound(String filename, int Id){
            this.filename=filename;
            this.Id=Id;
        }
        public String getFileName() { return filename;}
        public int getID() { return Id; }
    }

    public static synchronized Audio_Manager getInstance(){
        if(instance==null){
            instance=new Audio_Manager();
        }
        return instance;
    }

    public void Initialize(Context context){

        this.context=context;
        Music=new MediaPlayer();
        Music.setOnCompletionListener(this);
        Music.setOnPreparedListener(this);
        assets=context.getResources().getAssets();

    }

    private boolean containsSound(String file){
        for(Sound s: SoundList){
            if(s.getFileName().equals(file))return true;
        }
        return false;
    }

    private boolean containsSound(int id){
        for(Sound s: SoundList){
            if(s.getID()==id)return true;
        }
        return false;
    }

    public int addSound(String filename){
        if(!containsSound(filename)){
            try{

                AssetFileDescriptor assetDesc=assets.openFd(filename);
                int soundId= Sounds.load(assetDesc,1);
                SoundList.add(new Sound(filename,soundId));
                return soundId;
            }
            catch(Exception e){ }
        }
        return -1;
    }
    public void playSound(int soundId){
       if(containsSound(soundId)) Sounds.play(soundId,GameVolume,GameVolume,0,0,1);
    }

    public void unloadSound(int soundId){
        if(containsSound(soundId)) Sounds.unload(soundId);
    }

    public void setVolume(float volume){
        setSoundVolume(volume);
        setMusicVolume(volume);
        GameVolume=volume;
    }

    public void LoadMusic(String file){
        try{
            AssetFileDescriptor assetDesc=assets.openFd(file);
            if (isMusicPlaying()) Music.stop();
            Music.reset();
            Music.setDataSource(assetDesc.getFileDescriptor(),
                    assetDesc.getStartOffset(),assetDesc.getLength());
            //Don't block the main thread.
            Music.prepare();
            Music.setVolume(GameVolume, GameVolume);
            Music.start();

        }
        catch(Exception e){}
    }

    public boolean isMusicLooping() {return Music.isLooping();}
    public boolean isMusicPlaying(){ return Music.isPlaying(); }
    public boolean isMusicStopped(){ return !isPrepared;}

    public void playMusic(){
        if (Music.isPlaying()) return;
        try{
            synchronized (this){
                if(!isPrepared) Music.prepare();
                Music.start();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void stopMusic(){
        Music.stop();
        synchronized (this){
            isPrepared=false;
        }
    }

    public void setMusicLooping(boolean looping){
        Music.setLooping(looping);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        synchronized (this){ isPrepared=true;  }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        synchronized (this){ isPrepared=false; }
    }

    private void setMusicVolume(float volume){
        Music.setVolume(volume,volume);
    }
    private void setSoundVolume(float volume){
        for(Sound s: SoundList){
            Sounds.setVolume(s.getID(), volume, volume);
        }
    }

    /**
     * Stops and unloads all currently playing sounds.
     */
    public void unloadSounds(){
        for(Sound s: SoundList){
            Sounds.stop(s.getID());
            Sounds.unload(s.getID());
        }
        SoundList.clear();
    }

    public void unload_Sounds(){
        for(Sound s:SoundList){
            Sounds.stop(s.getID());
        }
    }
    /**
     * Unloads the current song, and prevents the user from attempting to restart it.
     * (Note: This method can be used at runtime)
     */
    public synchronized void unloadMusic(){
        if(Music!=null){
            if (Music.isPlaying()) {
                Music.stop();
                isPrepared=false;
            }
            Music.reset();
        }
    }

    public void unloadAtRuntime(){
        unloadSounds();
        unloadMusic();
    }
    /**
     * Stops the currently playing music, and releases resources.
     */
    private void unload_Music(){
        if(Music!=null){
            if(Music.isPlaying()) Music.stop();
            Music.release();
            Music=null;
        }

    }

    /**
     * Releases all sound resources.
     * Note: Do not call this in any method except onPause()
     */
    public void unload(){
        unloadSounds();
        unload_Music();
    }
}
