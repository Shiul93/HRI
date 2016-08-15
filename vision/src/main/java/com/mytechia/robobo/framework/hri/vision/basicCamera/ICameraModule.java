package com.mytechia.robobo.framework.hri.vision.basicCamera;

import android.view.TextureView;

import com.mytechia.robobo.framework.IModule;

/**
 * Created by luis on 19/7/16.
 */
public interface ICameraModule extends IModule {

    public void suscribe(ICameraListener listener);

    public void unsuscribe(ICameraListener listener);

    public void signalInit();
}
