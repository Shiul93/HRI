package com.mytechia.robobo.framework.hri.speech.recognition;

import java.util.HashSet;

/**
 * Created by luis on 5/4/16.
 */
public abstract class ASpeechRecognitionModule implements ISpeechRecognitionModule{
    private HashSet<ISpeechRecognitionListener> listeners;
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

    protected void notifyPhrase(String phrase, Long timestamp){
        for (ISpeechRecognitionListener listener:listeners){
            listener.phraseRecognized(phrase,timestamp);
        }
    }

    protected void notifyStartup(){
        for (ISpeechRecognitionListener listener:listeners){
            listener.onModuleStart();
        }
    }
}
