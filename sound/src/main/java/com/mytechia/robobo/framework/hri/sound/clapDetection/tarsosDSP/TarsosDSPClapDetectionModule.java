package com.mytechia.robobo.framework.hri.sound.clapDetection.tarsosDSP;

import android.content.res.AssetManager;
import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.commons.util.thread.Threads;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.sound.clapDetection.AClapDetectionModule;
import com.mytechia.robobo.framework.hri.sound.soundDispatcherModule.ISoundDispatcherModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import be.tarsos.dsp.AudioDispatcher;

/**
 * Created by luis on 25/7/16.
 */
public class TarsosDSPClapDetectionModule extends AClapDetectionModule {

    //region VAR

    private String TAG = "TarsosClapModule";

    private PercussionOnsetDetector mPercussionDetector;

    private ISoundDispatcherModule dispatcherModule;

    private double threshold = 8;

    private double sensitivity = 20;

    private float samplerate = 44100;

    private int buffersize = 2048;

    //endregion

    //region IModule methods
    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        dispatcherModule = manager.getModuleInstance(ISoundDispatcherModule.class);
        Properties properties = new Properties();
        AssetManager assetManager = manager.getApplicationContext().getAssets();

        try {
            InputStream inputStream = assetManager.open("sound.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        samplerate = Integer.parseInt(properties.getProperty("samplerate"));
        buffersize = Integer.parseInt(properties.getProperty("buffersize"));
        sensitivity = Integer.parseInt(properties.getProperty("clap_sensitivity"));
        threshold = Integer.parseInt(properties.getProperty("clap_threshold"));

        Log.d(TAG,"Properties loaded: "+samplerate+" "+buffersize);




        mPercussionDetector = new PercussionOnsetDetector(samplerate, buffersize,
                new OnsetHandler() {

                    @Override
                    public void handleOnset(double time, double salience) {
                        Log.d(TAG, "Clap detected!");
                        notifyClap(time);
                    }
                }, sensitivity, threshold);


        dispatcherModule.addProcessor(mPercussionDetector);
    }

    @Override
    public void shutdown() throws InternalErrorException {
        dispatcherModule.removeProcessor(mPercussionDetector);
        mPercussionDetector.processingFinished();

    }

    @Override
    public String getModuleInfo() {
        return "Clap detection Module";
    }

    @Override
    public String getModuleVersion() {
        return "v0.1";
    }
    //endregion


}
