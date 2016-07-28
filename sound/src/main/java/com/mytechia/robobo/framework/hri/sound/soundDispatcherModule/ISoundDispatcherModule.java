package com.mytechia.robobo.framework.hri.sound.soundDispatcherModule;

import com.mytechia.robobo.framework.IModule;

import be.tarsos.dsp.AudioProcessor;

/**
 * Created by luis on 26/7/16.
 */
public interface ISoundDispatcherModule extends IModule {
    public void addProcessor(AudioProcessor processor);
    public void removeProcessor(AudioProcessor processor);
    public void runDispatcher();
    public void stopDispatcher();
}
