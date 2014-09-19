package com.weight.craig.catshanks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.graphics.PointF;
//import android.util.Log;

import java.nio.IntBuffer;
import java.util.ArrayList;

/**
 * A texture manager loads and manages texture resources.
 * Todo:Fix unloading routines.
 * Created by Craig on 11/21/13.
 */
public class TextureManager {
    /**
     * A texture..Enough said.
     */
    private class Texture{
        private int textureID;
        private int drawableID;
        private PointF Size=new PointF();
        public Texture(int textureId,int drawableID,int width,int height){
            this.textureID=textureId;
            this.drawableID=drawableID;
            Size.set(width,height);
        }
        public PointF getSize(){ return Size; }
        public int getResourceId(){ return drawableID; }
        public int getTextureId(){ return textureID; }
    }

    ////////
    //Vars
    ////////
    private static TextureManager instance;
    private Context context=null;
    private ArrayList<Texture> Textures=new ArrayList<Texture>();

    private TextureManager () {}

    /**
     * Provides an entry point with which to access the texture manager.
     * @return Entry point into texture manager.
     */
    public static synchronized TextureManager getInstance(){
        if(instance==null) instance=new TextureManager();
        return instance;
    }

    /**
     * Provides the texture manager access to program resources.
     * @param context Applications context.
     */
    public void setContext(Context context){
        this.context=context;
    }

    /**
     * Removes all texture references.
     */
    public void clear() { Textures.clear(); }

    /**
     * Gets the textureId of a given resource.
     * @param resource The resources unique identifier.
     * @return Texture Id >=0 if successful, otherwise -1;
     */
    public int getResourceTextureID(int resource){
        for(Texture texture:Textures){
            if(texture.getResourceId()==resource){
                return texture.getTextureId();
            }
        }
        return -1;
    }


    /**
     * Unloads a given texture(Note this does not work right now!! Not sure why)
     * Todo: Fix this!!
     * @param texture Unique texture id.
     */
    public synchronized void unloadTexture(int texture){
        int TextureIndex=getResourceTextureID(texture);

        //Check to see if the texture exists, if it does then unload it.
        if(TextureIndex!=-1){
            //Create a temp intbuffer to unload the given texture.
            IntBuffer textures= IntBuffer.allocate(1);
            textures.position(0);
            textures.put(texture);
            GLES20.glDeleteTextures(1,textures);
            //Log.i("Errors?",""+GLES20.glGetError());

            //Remove the reference to the texture.
            Textures.remove(TextureIndex);
        }
    }


    /**
     * Unloads all textures in the current texture manager. (Note: Does not work!!)
     * Todo:Fix this!!
     */
    public void unloadAllTextures(){
        //Create a intBuffer used for unloading with opengl
        IntBuffer textures= IntBuffer.allocate(Textures.size());
        textures.position(0);
        for(Texture texture: Textures) textures.put(texture.getTextureId());
        //Delete the textures.
        GLES20.glDeleteTextures(Textures.size(),textures);

        clear();
    }

    /**
     * Returns whether the current texture has been loaded by the textureManager.
     * @param id Texture resource Id.
     * @return True if loaded, otherwise false.
     */
    public boolean containsTexture(int id) {
        for(Texture texture:Textures){
            if(texture.getTextureId()==id) return true;
        }
        return false;
    }

    /**
     * Gets the index of a texture.
     * @param texture Texture Id.
     * @return Index >=0 if successful, otherwise -1;
     */
    public int getTextureIndex(int texture){
        for(int i=0; i<Textures.size();i++){
            if (Textures.get(i).getTextureId()==texture) return i;
        }
        return -1;
    }

    /**
     * Gets the size of a texture
     * @param texture Texture Id.
     * @return Textures dimensions if successful, otherwise null.
     */
    public PointF getTextureSize(int texture){
        int textureindex=getTextureIndex(texture);
        if(textureindex!=-1) return Textures.get(textureindex).getSize();
        return null;
    }

    /**
     * Adds a texture to the texture manager.
     * @param drawable Resources Unique Id.
     * @return Texture id >=0 if successful, otherwise -1;
     */
    public int addTexture(int drawable){
        if (context!=null){     //Check to see if the context has been set for this manager.
            try{
                int rtexture=getResourceTextureID(drawable);

                //Check to see if the texture has already been added to the ArrayList
                //(Prevents loading the same texture twice)
                if(rtexture==-1){
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inScaled = false;

                    Bitmap bmptexture= BitmapFactory.decodeResource(context.getResources(), drawable,opts);  //Load the bitmap

                    IntBuffer textureID=IntBuffer.allocate(1);                                          //Create a textureID buffer.
                    GLES20.glGenTextures(1,textureID);                                                  //Generate an Id for the texture.
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureID.get(0));
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bmptexture,0);                            //Pass the texture to openGL
                    Textures.add(new Texture(textureID.get(0), drawable, bmptexture.getWidth(), bmptexture.getHeight()));//Add the textures to the texture list.
                    bmptexture.recycle();                                                               //Release the resource.
                    return textureID.get(0);
                }
                else return rtexture;
            }
            catch(Exception e) {}                                                                       //Deal with loading errors.
        }
        return -1;
    }
}
