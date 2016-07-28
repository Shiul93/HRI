package com.mytechia.robobo.framework.hri.sound.soundDispatcherModule.TarsosDSP;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.sound.soundDispatcherModule.ASoundDispatcherModule;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;

/**
 * Created by luis on 26/7/16.
 */
public class TarsosDSPSoundDispatcherModule extends ASoundDispatcherModule {

    private String TAG = "TarsosDispatcherModule";
    private AudioDispatcher dispatcher;
    private Thread dispatcherThread;

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
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,2048,0);
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
