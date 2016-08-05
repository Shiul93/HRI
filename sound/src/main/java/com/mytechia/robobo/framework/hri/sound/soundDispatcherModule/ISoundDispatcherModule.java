package com.mytechia.robobo.framework.hri.sound.soundDispatcherModule;

import com.mytechia.robobo.framework.IModule;

import be.tarsos.dsp.AudioProcessor;

/**
 * Created by luis on 26/7/16.
 */
public interface ISoundDispatcherModule extends IModule {
    /**
     * Adds a sound processor to the list of active processors
     * @param processor The processor
     */
    public void addProcessor(AudioProcessor processor);

    /**
     * Removes a sound processor from the list of active processors
     * @param processor The processor
     */
    public void removeProcessor(AudioProcessor processor);

    /**
     * Starts the sound dispatcher
     */
    public void runDispatcher();

    /**
     * Stops the sound dispatcher
     */
    public void stopDispatcher();
}
