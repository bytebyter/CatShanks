package com.weight.craig.catshanks.Support;

import java.util.Arrays;

/**
 * Created by Craig on 8/19/2014.
 */
public class Int_Buffer {
    private int [] arr=new int[1];
    private int size=0;

    public Int_Buffer(){}

    public void add(int num ){
        if(size==arr.length) arr= Arrays.copyOf(arr,arr.length*2);
        arr[size++]=num;
    }

    public int get(int index){
        if(index<size) return arr[index];
        return -1;
    }

    public boolean contains(int num){
        for(int i=0; i<size; i++){
            if(arr[i]==num) return true;
        }
        return false;
    }

    public void removeAt(int index){
        if(index>0 && index<size) arr[index]=arr[size-1];
        size--;
    }

    public void remove(int num){
        for(int i=0; i<size; i++){
            if(arr[i]==num)  {
                arr[i]=arr[size-1];
                return;
            }
        }
    }
    public void clear(){
        size=0;
    }
    public int size(){ return size; }

}
