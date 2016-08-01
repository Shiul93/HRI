package com.mytechia.robobo.framework.hri.sound.noteDetection;

import com.mytechia.robobo.framework.hri.sound.pitchDetection.IPitchListener;

import java.util.HashSet;

/**
 * Created by luis on 30/7/16.
 */
public abstract class ANoteDetectionModule implements INoteDetectionModule {
    public HashSet<INoteListener> listeners;

    public double freqToNote(double freq){

        //freq = 440* 2^(n/12)
        //http://www.intmath.com/trigonometric-graphs/music.php
        double note = 0.0;

        double noteaprox = (Math.log(freq/440.0)/Math.log(2))*12.0;

        return noteaprox;
    }

    public ANoteDetectionModule(){
        listeners = new HashSet<INoteListener>();
    }
    public void suscribe(INoteListener listener){
        listeners.add(listener);
    }
    public void unsuscribe(INoteListener listener){
        listeners.remove(listener);
    }

    public void notifyNote(Note note){
        for(INoteListener listener:listeners){
            listener.onNoteDetected(note);

        }
    }
}
