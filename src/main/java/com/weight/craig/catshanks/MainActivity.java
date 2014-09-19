package com.weight.craig.catshanks;

import android.annotation.TargetApi;
import android.graphics.PointF;
import android.media.AudioManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends Activity implements GLSurfaceView.Renderer {
    private TextureManager TM=TextureManager.getInstance();
    private TextureBatcher Batcher= TextureBatcher.getInstance();
    private Audio_Manager audioManager=Audio_Manager.getInstance();

    private GLSurfaceView glView;
    private Screen gameScreen;
    FPSCounter fps=new FPSCounter();

    private long oldTime=0;
    private long beginingTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioManager.Initialize(this);
        TM.setContext(this);

        //Remove the title bar.
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener(){
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSystemUiVisibilityChange(int i) { init_FullScreenMode();  }
        });

        //Set audio up so that the volume can be controlled by the volume control buttons.
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //Instantiate the GLSurfaceView
        glView= new GLSurfaceView(this);
        glView.setEGLContextClientVersion(2);
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.setRenderer(this);

        //Have the onTouchListener send it's events to the touch manager.
        glView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //touchManager.onTouch(event);
                //touchManager.printDebugInfo();
                if(gameScreen!=null){gameScreen.onTouch(event);}
                return true;
            }
        });
        setContentView(glView);
    }

    @Override
    protected void onPause() {

        super.onPause();
        if(gameScreen!=null) gameScreen.onPause();
        glView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(gameScreen!=null) gameScreen.onResume();
        glView.onResume();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {


        GLES20.glClearColor(1,1,1,1);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        //TM.clear();
        Batcher.Initialize();
        AnimationManager.getInstance().Initialize(this);
        TM.clear();
        gameScreen=new IntroScreen(this);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        GLES20.glViewport(0,0,width,height);    //Tell the viewport to stretch to the width/height
        Resolution.getInstance().setResolution(new PointF(width,height));
        Resolution.getInstance().setScale(((float)(width*.50)/768));
        Batcher.setSize(width,height);
        if (gameScreen!=null) gameScreen.setScreenSize(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        oldTime=beginingTime;
        beginingTime=System.nanoTime();
        if(gameScreen!=null &&oldTime!=0){gameScreen.update((float)(Math.pow(10,-9)) * (beginingTime-oldTime));}
        fps.logFrame();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        init_FullScreenMode();
    }


    private void init_FullScreenMode(){
        //Force Full Screen...Doing this here prevents the awkward resolution change on loading.
        if (Build.VERSION.SDK_INT <  19) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
            getWindow().setBackgroundDrawable(null);
            //getWindow().getDecorView().setBackground(null);
        }
    }

    private class FPSCounter {
        long startTime = System.nanoTime();
        int frames = 0;
        public void logFrame() {
            frames++;
            if(System.nanoTime() - startTime >= 1000000000) {
                Log.d("FPSCounter", "fps: " + frames);
                frames = 0;
                startTime = System.nanoTime();
            }
        }
    }
}
