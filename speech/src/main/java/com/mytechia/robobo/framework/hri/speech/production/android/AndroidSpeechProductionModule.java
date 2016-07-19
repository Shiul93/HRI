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

    private TextToSpeech tts = null;
    private Locale loc = null;
    private Context context = null;


    @Override
    /**
     * Says a text through the phone speaker
     * @param text The text to be said
     * @param priority The priority of the phrase, ISpeechProductionModule.PRIORITY_HIGH / LOW
     */
    public void sayText(String text, Integer priority) {

       /* if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){

        } else{
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);

        }*/
        if (priority == ISpeechProductionModule.PRIORITY_HIGH){
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }else
        if (priority == ISpeechProductionModule.PRIORITY_LOW){
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
    }

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
    public void selectVoice(String name) throws VoiceNotFoundException, UnsupportedOperationException{

        //Check if the android version of the device supports voices for the tts



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


        Collection<Voice> voices = tts.getVoices();
        for (Voice v : voices) {
            results.add(v.getName());
        }
        return results;



    }

    public Collection<ITtsVoice> getVoices() throws  UnsupportedOperationException{


        //TODO Duda: ¿Generar la lista de voces al crear el tts?
        //TODO Descartar las que requienren conexion a internet?

        Collection<ITtsVoice> results = new ArrayList<>();


        Collection<Voice> voices = tts.getVoices();
        for (Voice v : voices) {
            if (!v.isNetworkConnectionRequired()){
                ITtsVoice ttsV = new AndroidTtsVoice(v);
                results.add(ttsV);
            }

        }
        return results;




        /*
        */



    }

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
            }

        }
        );


    }

    //TODO Provisional a espera de saber utilizar el FrameworkManager
    public void startupTest(Context co) throws InternalErrorException {
        context = co;

        //Default language, English
        loc = Locale.getDefault();

        //Creation the TTS object
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                tts.setLanguage(loc);
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


    //TODO Nuevos metodos, preguntar que se pone
    @Override
    public String getModuleInfo() {
        return "Android Speech Production Module";
    }

    @Override
    public String getModuleVersion() {
        return "0.1";
    }


}
