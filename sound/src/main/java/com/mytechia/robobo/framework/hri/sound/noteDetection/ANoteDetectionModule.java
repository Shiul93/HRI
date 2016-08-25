package com.mytechia.robobo.framework.hri.sound.noteDetection;

import com.mytechia.robobo.framework.hri.sound.pitchDetection.IPitchListener;

import java.util.HashSet;

/**
 * Created by luis on 30/7/16.
 */
public abstract class ANoteDetectionModule implements INoteDetectionModule {
    private HashSet<INoteListener> listeners;



    public ANoteDetectionModule(){
        listeners = new HashSet<INoteListener>();
    }
    public void suscribe(INoteListener listener){
        listeners.add(listener);
    }
    public void unsuscribe(INoteListener listener){
        listeners.remove(listener);
    }

    /**
     * Notifies when an note is being played
     * @param note The note being played
     */
    protected void notifyNote(Note note){
        for(INoteListener listener:listeners){
            listener.onNoteDetected(note);

        }
    }

    /**
     * Notifies when a note stops playing
     * @param note The note
     * @param time The time elapsed by the note
     */
    protected void notifyNoteEnd(Note note, long time){
        for(INoteListener listener:listeners){
            listener.onNoteEnd(note, time);

        }
    }

    /**
     * Notifies qhen a note starts playing
     * @param note The note
     */
    protected void notifyNewNote(Note note){
        for(INoteListener listener:listeners){
            listener.onNewNote(note);

        }
    }

    /**
     * Converts frequency into note index
     * @param freq The frequency to be converted
     * @return the index of the note
     */
    protected double freqToNote(double freq){

        //freq = 440* 2^(n/12)
        //http://www.intmath.com/trigonometric-graphs/music.php
        double note = 0.0;

        double noteaprox = (Math.log(freq/220.0)/Math.log(2))*12.0;

        return noteaprox;
    }
}
