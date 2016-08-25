package com.mytechia.robobo.framework.hri.speech.production.android;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.speech.production.ISpeechProductionModule;
import com.mytechia.robobo.framework.hri.speech.production.ITtsVoice;
import com.mytechia.robobo.framework.hri.speech.production.VoiceNotFoundException;


/**
 * Created by luis on 5/4/16.
 */
public class AndroidSpeechProductionModule implements ISpeechProductionModule {

    //region VAR
    private TextToSpeech tts = null;
    private Locale loc = null;
    private Context context = null;
    Collection<ITtsVoice> voices = null;
    //endregion

    //region ISpeechProductionModule Methods
    @Override
    /**
     * Says a text through the phone speaker
     * @param text The text to be said
     * @param priority The priority of the phrase, ISpeechProductionModule.PRIORITY_HIGH / LOW
     */
    public void sayText(String text, Integer priority) {

        if (priority == ISpeechProductionModule.PRIORITY_HIGH){
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }else
        if (priority == ISpeechProductionModule.PRIORITY_LOW){
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
    }

    @Override
    /**
     * Sets a new locale for the Text To Speech object
     * @param newloc new Locale to set
     */
    public void setLocale(Locale newloc){

        loc = newloc;
        tts.setLanguage(loc);
    }

    @Override
    /**
     *  Sets the current voice of the text to speech generator
     *  @param name The name of the voice to use
     *  @throws VoiceNotFoundException
     */
    public void selectVoice(String name) throws VoiceNotFoundException{


        //Iterate over the voices and if the desired voice is found, set it in the tts object

        Voice v = null;
        Collection<Voice> voices = tts.getVoices();



        //Iterate over the collection searching for the required voice
        for (Voice vo : voices) {
            if (vo.getName().equals(name)){
                v = vo;
            }
        }

        //Throw exception if no suitable voice is found
        if (v == null){
            Log.e("TTS","Error: voice "+name+"not found");
            throw new VoiceNotFoundException("Voice "+name+" not found");


        }

        tts.setVoice(v);


    }
    @Override
    /**
     * Sets a voice on the text to speech engine
     * @param voice The  voice to use
     * @throws VoiceNotFoundException
     */
    public void selectTtsVoice(ITtsVoice voice) throws VoiceNotFoundException{


        tts.setVoice(((AndroidTtsVoice) voice).getInternalVoice());


    }

    @Override
    /**
     *  Returns a collection of the available voices for text to speech
     *  @return A collection of the available voices names
     */
    public Collection<String> getStringVoices(){



        Collection<String> results = new ArrayList<String>();

        for (ITtsVoice v : this.voices) {

            results.add(v.getVoiceName());
        }
        return results;



    }
    @Override
    public Collection<ITtsVoice> getVoices(){

        return this.voices;

    }
    //endregion

    //region IModule Methods
    @Override
    /**
     *  Starts the TextToSpeech engine
     *  @param frameworkManager instance of the Robobo framework manager
     *  @throws InternalErrorException
     */

    public void startup(RoboboManager roboboManager) throws InternalErrorException {
        context = roboboManager.getApplicationContext();

        //Default language of the OS
        loc = Locale.getDefault();


        //Creation the TTS object
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                tts.setLanguage(loc);
                voices = new ArrayList<>();
                Collection<Voice> voicesColl = tts.getVoices();
                for (Voice v : voicesColl) {
                    if (!v.isNetworkConnectionRequired()){
                        ITtsVoice ttsV = new AndroidTtsVoice(v);
                        voices.add(ttsV);
                    }

                }
            }

        }
        );







    }



    @Override
    /**
     * Stops the TextToSpeech engine and frees the resources
     * @throws InternalErrorException
     */
    public void shutdown() throws InternalErrorException {
        //Liberación de recursos del text to speech
        tts.shutdown();

    }



    @Override
    public String getModuleInfo() {
        return "Android Speech Production Module";
    }

    @Override
    public String getModuleVersion() {
        return "0.1";
    }

    //endregion

}
