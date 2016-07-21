package com.mytechia.robobo.framework.hri.vision;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import com.mytechia.robobo.framework.hri.vision.android.Frame;

import java.util.HashSet;

/**
 * Created by luis on 19/7/16.
 */
public abstract class ACameraModule implements ICameraModule{
    private HashSet<ICameraListener> listeners;
    public ACameraModule(){
        listeners = new HashSet<ICameraListener>();
    }

    protected void notifyFrame(Frame frame){
        for (ICameraListener listener:listeners){
                listener.onNewFrame(frame);
        }
    }

    public void suscribe(ICameraListener listener){
        Log.d("AV_module", "Suscribed:"+listener.toString());
        listeners.add(listener);
    }
    public void unsuscribe(ICameraListener listener){
        listeners.remove(listener);
    }
}
