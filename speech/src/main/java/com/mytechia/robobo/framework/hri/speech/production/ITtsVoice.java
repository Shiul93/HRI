package com.mytechia.robobo.framework.hri.speech.production;

/**
 * Created by luis on 13/6/16.
 */

/**
 * Common interface form managing voices
 */
public interface ITtsVoice {
    /**
     * Get voice name
     * @return The name of the voice
     */
    String getVoiceName();

    /**
     * Get the language of the voice
     * @return the Locale string
     */
    String getVoiceLanguage();


}
