package com.mytechia.robobo.framework.hri.sound.noteDetection;

import com.mytechia.robobo.framework.IModule;

/**
 * Created by luis on 26/7/16.
 */
public interface INoteDetectionModule extends IModule{
    public void suscribe(INoteListener listener);

    public void unsuscribe(INoteListener listener);



}
