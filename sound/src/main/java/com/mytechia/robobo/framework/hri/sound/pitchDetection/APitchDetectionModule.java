package com.mytechia.robobo.framework.hri.sound.pitchDetection;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by luis on 26/7/16.
 */
public abstract class APitchDetectionModule implements IPitchDetectionModule {
    public HashSet<IPitchListener> listeners;
    public APitchDetectionModule(){
        listeners = new HashSet<IPitchListener>();
    }
    public void suscribe(IPitchListener listener){
        listeners.add(listener);
    }
    public void unsuscribe(IPitchListener listener){
        listeners.remove(listener);
    }


    /**
     * Notifies when a pitch is detected
     * @param freq The frequency of the sound
     */
    public void notifyPitch(double freq){
        for (IPitchListener listener:listeners){
            listener.onPitchdetected(freq);
        }
    }

}
