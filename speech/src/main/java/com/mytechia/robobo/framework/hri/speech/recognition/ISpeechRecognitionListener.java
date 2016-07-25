package com.mytechia.robobo.framework.hri.speech.recognition;

/**
 * Created by luis on 5/4/16.
 */
public interface ISpeechRecognitionListener {

    /**
     * Notifies when a phrase is recognized
     * @param phrase The recognized phrease
     * @param timestamp Timestamp of the detection
     */
    void phraseRecognized(String phrase, Long timestamp);

    /**
     * Notifies when the speech recognizer is ready to work
     * Add phrases inside of the implementation of this method,
     * otherwise it will crash
     */
    void onModuleStart();
}
