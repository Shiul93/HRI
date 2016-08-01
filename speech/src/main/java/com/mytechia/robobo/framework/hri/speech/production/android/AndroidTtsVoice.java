package com.mytechia.robobo.framework.hri.speech.production.android;


import android.speech.tts.Voice;

import com.mytechia.robobo.framework.hri.speech.production.ITtsVoice;

import java.util.Locale;

/**
 * Created by luis on 2/5/16.
 */

/**
 * Implementation of the Tts voice interface for the Android TTS module
 */
public class AndroidTtsVoice implements ITtsVoice{


    //Voice object to wrap
    private Voice internalVoice = null;
    //Name of the voice
    private String voiceName = null;
    //Locale of the voice
    private Locale loc = null;
    //Name of the language
    private String voiceLanguage = null;



    public AndroidTtsVoice(Voice vo){
        internalVoice = vo;
        loc = vo.getLocale();
        voiceLanguage = loc.getLanguage();
        voiceName = vo.getName();

    }

    @Override
    public String getVoiceName() {
        return voiceName;
    }

    @Override
    public String getVoiceLanguage() {
        return voiceLanguage;
    }

    @Override
    public String toString() {
        return "TtsVoice{" +
                "voiceName='" + voiceName + '\'' +
                ", voiceLanguage='" + voiceLanguage + '\'' +
                '}';
    }

    public Voice getInternalVoice() {
        return internalVoice;
    }
}
