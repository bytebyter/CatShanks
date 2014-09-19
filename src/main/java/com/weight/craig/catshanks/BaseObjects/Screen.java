package com.weight.craig.catshanks.BaseObjects;

import android.view.MotionEvent;

import com.weight.craig.catshanks.Managers.Audio_Manager;
import com.weight.craig.catshanks.Managers.TextureBatcher;
import com.weight.craig.catshanks.Managers.TextureManager;

/**
 * Created by Craig on 11/27/13.
 */
public interface Screen {
    TextureManager textureManager= TextureManager.getInstance();
    TextureBatcher Batcher= TextureBatcher.getInstance();
    Audio_Manager audioManager= Audio_Manager.getInstance();

    public void update(float deltaTime);

    public void onPause();
    public void onResume();

    public void setScreenSize(int width, int height);
    public Screen getScreen();
    public void onTouch(MotionEvent event);
}
