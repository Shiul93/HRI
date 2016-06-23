package com.mytechia.robobo.framework.hri.speech.production;


import com.mytechia.robobo.framework.IModule;

import java.util.Collection;

/**
 * Created by luis on 5/4/16.
 */
public interface ISpeechProductionModule extends IModule {
    Integer PRIORITY_HIGH = 1;
    Integer PRIORITY_LOW = 0;


    /**
     * Says the text through the phone speakers.
     * @param text The text to be said
     * @param priority The priority of the speech (PRIORITY_HIGH, PRIORITY_LOW)
     */
    void sayText(String text, Integer priority);


    /**
     *  Sets the current voice of the text to speech generator
     *  @param name The name of the voice to use
     *  @throws VoiceNotFoundException, UnsupportedOperationException
     */
    void selectVoice(String name) throws Exception;


    /**
     *  Sets the current voice of the text to speech generator
     *  @param voice The  voice to use
     *  @throws VoiceNotFoundException, UnsupportedOperationException
     */
    void selectTtsVoice(ITtsVoice voice) throws Exception;

    /**
     *  Returns a collection of the available voices for text to speech
     *  @return A collection of the available voices
     *  @throws UnsupportedOperationException
     */
    Collection<ITtsVoice> getVoices();

    /**
     * Returns a collection of the voice names
     * @return  A collection of the available voices names
     */
    Collection<String> getStringVoices();
}
