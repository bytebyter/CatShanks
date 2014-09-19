package com.weight.craig.catshanks;

import android.content.Context;
import android.graphics.RectF;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by Craig on 12/15/13.
 */
public class AnimationManager {
    private static AnimationManager instance;
    private HashMap<String,HashMap<String, SpriteAnimation>> AnimationSets=new HashMap<String, HashMap<String, SpriteAnimation>>();

    public static synchronized AnimationManager getInstance(){
        if(instance==null) {
            instance=new AnimationManager();
        }
        return instance;
    }
    private AnimationManager(){}

    public HashMap<String,SpriteAnimation> getAnimationSet(String AnimationSet){
        return AnimationSets.get(AnimationSet);
    }

    public void Initialize(Context context){
        int AnimationSetCount,AnimationCount,FrameCount;
        int [] coordinates= new int [4];
        int [] colCoordinates=new int[4];
        boolean isCircle;

        String AnimationSetName,AnimationName,Frame,CollisionBox;
        SpriteAnimation aSet;

        try{
            InputStream inputStream = context.getResources().openRawResource(R.raw.animations);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            //Read the animation set descriptor file.
            reader.readLine();
            AnimationSetCount=Integer.parseInt(reader.readLine());
            for(int i=0;i<AnimationSetCount;i++){
                reader.readLine();
                reader.readLine();
                AnimationSetName=reader.readLine();
                aSet=new SpriteAnimation();
                AnimationCount=(Integer.parseInt(reader.readLine()));
                for(int j=0;j<AnimationCount;j++){
                    AnimationName=reader.readLine();
                    FrameCount=(Integer.parseInt(reader.readLine()));
                    for(int k=0;k<FrameCount;k++){
                        Frame= reader.readLine();
                        CollisionBox=reader.readLine();
                        int index=0;
                        for(String Coordinate: Frame.split(",")){
                            coordinates[index]=Integer.parseInt(Coordinate);
                            index++;
                        }

                        index=0;
                        isCircle=(CollisionBox.charAt(0)=='C');
                        String []Coor=CollisionBox.split(",");
                        for(String C: Coor){
                            if(index!=0){
                                colCoordinates[index-1]=Integer.parseInt(C);
                            }
                            index++;
                        }
                        //index=0;
                        if (isCircle) aSet.addFrame(coordinates[0],coordinates[1],coordinates[2],coordinates[3],
                                new ShapeF(new Circle(0,0,colCoordinates[0])));
                        else aSet.addFrame(coordinates[0],coordinates[1],coordinates[2],coordinates[3],
                                new ShapeF(new RectF(colCoordinates[0],colCoordinates[1],colCoordinates[2],colCoordinates[3])));

                    }
                    if (AnimationSets.containsKey(AnimationSetName)) AnimationSets.get(AnimationSetName).put(AnimationName,aSet);
                    else {
                        AnimationSets.put(AnimationSetName,new HashMap<String,SpriteAnimation>());
                        AnimationSets.get(AnimationSetName).put(AnimationName,aSet);
                    }
                }

            }
        }
        catch(Exception e){}
    }
}
