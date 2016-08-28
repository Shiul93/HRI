package com.mytechia.robobo.framework.hri.sound.soundDispatcherModule.TarsosDSP;

import android.content.res.AssetManager;
import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.sound.soundDispatcherModule.ISoundDispatcherModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;

/**
 * Created by luis on 26/7/16.
 */
public class TarsosDSPSoundDispatcherModule implements ISoundDispatcherModule {

    private String TAG = "TarsosDispatcherModule";
    private AudioDispatcher dispatcher;
    private Thread dispatcherThread;

    private int samplerate = 22050;

    private int buffersize = 2048;

    private int overlap = 0;

    //region SoundDispatcherModule methods
    @Override
    public void addProcessor(AudioProcessor processor) {
        dispatcher.addAudioProcessor(processor);

    }

    @Override
    public void removeProcessor(AudioProcessor processor) {
        dispatcher.removeAudioProcessor(processor);
    }

    @Override
    public void runDispatcher() {
        dispatcherThread = new Thread(dispatcher);
        dispatcherThread.run();
    }

    @Override
    public void stopDispatcher() {
        dispatcher.stop();
    }
    //endregion

    //region IModule Methods
    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
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
        overlap = Integer.parseInt(properties.getProperty("overlap"));
        Log.d(TAG,"Properties loaded: "+samplerate+" "+buffersize+" "+overlap);
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(samplerate,buffersize,overlap);
    }

    @Override
    public void shutdown() throws InternalErrorException {
        stopDispatcher();
    }

    @Override
    public String getModuleInfo() {
        return "Audio dispatcher module";
    }

    @Override
    public String getModuleVersion() {
        return "v0.1";
    }
    //endregion
}
