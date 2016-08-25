package com.mytechia.robobo.framework.hri.sound.noteGeneration;

import java.util.HashSet;

/**
 * Created by luis on 24/8/16.
 */
public abstract class ANoteGeneratorModule implements INoteGeneratorModule {
    public HashSet<INotePlayListener> listeners;
    public ANoteGeneratorModule(){
        listeners = new HashSet<INotePlayListener>();
    }
    public void suscribe(INotePlayListener listener){
        listeners.add(listener);
    }
    public void unsuscribe(INotePlayListener listener){
        listeners.remove(listener);
    }

    protected void notifyNotePlayEnd(){
        for (INotePlayListener listener:listeners){
            listener.onNotePlayEnd();
        }
    };
    protected void notifySequenceEnd(){
        for (INotePlayListener listener:listeners){
            listener.onSequencePlayEnd();
        }
    };

}
