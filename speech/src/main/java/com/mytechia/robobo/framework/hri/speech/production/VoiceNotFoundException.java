package com.mytechia.robobo.framework.hri.speech.production;

/**
 * Created by luis on 6/4/16.
 */
public class VoiceNotFoundException extends Exception {

    public VoiceNotFoundException () {

    }

    public VoiceNotFoundException (String message) {
        super (message);
    }

    public VoiceNotFoundException (Throwable cause) {
        super (cause);
    }

    public VoiceNotFoundException (String message, Throwable cause) {
        super (message, cause);
    }

}
