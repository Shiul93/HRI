package com.mytechia.robobo.framework.hri.vision;

import android.view.TextureView;

import com.mytechia.robobo.framework.IModule;

/**
 * Created by luis on 19/7/16.
 */
public interface ICameraModule extends IModule {
    public void foto();
    public void suscribe(ICameraListener listener);
    public void passTextureView(TextureView tv);
    public void unsuscribe(ICameraListener listener);
}
