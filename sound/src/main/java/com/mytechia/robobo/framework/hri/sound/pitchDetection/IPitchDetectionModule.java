package com.mytechia.robobo.framework.hri.sound.pitchDetection;

import com.mytechia.robobo.framework.IModule;

/**
 * Created by luis on 26/7/16.
 */
public interface IPitchDetectionModule extends IModule {

    public void suscribe(IPitchListener listener);

    public void unsuscribe(IPitchListener listener);
    
}
