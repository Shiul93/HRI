package com.mytechia.robobo.framework.hri.speech.recognition;

import java.util.HashSet;

/**
 * Created by luis on 5/4/16.
 */
public abstract class ASpeechRecognitionModule implements ISpeechRecognitionModule{
    //The set of listeners
    private HashSet<ISpeechRecognitionListener> listeners;

    //Class constructor
    public ASpeechRecognitionModule(){
        listeners = new HashSet<ISpeechRecognitionListener>();
    }


    @Override
    public void suscribe(ISpeechRecognitionListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unsuscribe(ISpeechRecognitionListener listener) {
        listeners.remove(listener);
    }


    /**
     * Notifies the listener when a phrase is recognized
     * @param phrase The phrase recognized
     * @param timestamp The time when the phrase was recognized
     */
    protected void notifyPhrase(String phrase, Long timestamp){
        for (ISpeechRecognitionListener listener:listeners){
            listener.phraseRecognized(phrase,timestamp);
        }
    }

    /**
     * Notifies the listeners the startup of the module
     */
    protected void notifyStartup(){
        for (ISpeechRecognitionListener listener:listeners){
            listener.onModuleStart();
        }
    }
}
