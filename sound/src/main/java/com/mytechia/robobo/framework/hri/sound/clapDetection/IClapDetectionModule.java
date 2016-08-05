package com.mytechia.robobo.framework.hri.sound.clapDetection;

import com.mytechia.robobo.framework.IModule;

/**
 * Created by luis on 25/7/16.
 */
public interface IClapDetectionModule extends IModule {
    public void suscribe(IClapListener listener);
    public void unsuscribe(IClapListener listener);



}
