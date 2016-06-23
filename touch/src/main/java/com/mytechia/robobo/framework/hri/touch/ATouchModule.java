package com.mytechia.robobo.framework.hri.touch;

import android.util.Log;

import java.util.HashSet;

/**
 * Created by luis on 5/4/16.
 */


public abstract class ATouchModule implements ITouchModule {
    private HashSet<ITouchListener> listeners;
    public ATouchModule(){
        listeners = new HashSet<ITouchListener>();
    }
    protected void notifyTap(Integer x, Integer y){
        for (ITouchListener listener:listeners){
            listener.tap(x, y);
        }
    }
    protected void notifyFling(TouchGestureDirection dir){
        for (ITouchListener listener:listeners){
            listener.fling(dir);
        }
    }
    protected void notifyCaress(TouchGestureDirection dir){
        for (ITouchListener listener:listeners){
            listener.caress(dir);
        }
    }
    protected void notifyTouch(Integer x, Integer y){
        for (ITouchListener listener:listeners){
            listener.touch(x,y);
        }
    }
    public void suscribe(ITouchListener listener){
        Log.d("AT_module", "Suscribed:"+listener.toString());
        listeners.add(listener);
    }
    public void unsuscribe(ITouchListener listener){
        listeners.remove(listener);
    }

}
