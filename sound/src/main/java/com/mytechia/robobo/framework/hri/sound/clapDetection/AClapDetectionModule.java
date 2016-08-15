package com.mytechia.robobo.framework.hri.sound.clapDetection;

import android.util.Log;

import java.util.HashSet;

/**
 * Created by luis on 25/7/16.
 */
public abstract class AClapDetectionModule implements IClapDetectionModule {
    private HashSet<IClapListener> listeners;

    public AClapDetectionModule(){
        listeners = new HashSet<IClapListener>();
    }

    @Override
    public void suscribe(IClapListener listener) {
        Log.d("FD_module", "Suscribed:"+listener.toString());
        listeners.add(listener);
    }

    @Override
    public void unsuscribe(IClapListener listener) {
        listeners.remove(listener);
    }


    /**
     * Notify the listeners of a clap
     * @param time Moment of the detection
     */
    protected void notifyClap(double time){
        for(IClapListener listener:listeners){
            listener.onClap(time);
        }
    }
}
