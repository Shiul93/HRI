package com.mytechia.robobo.framework.hri.vision.faceDetection;

import android.graphics.PointF;
import android.util.Log;

import com.mytechia.robobo.framework.IModule;

import java.util.HashSet;

/**
 * Created by luis on 24/7/16.
 */
public abstract class AFaceDetectionModule implements IFaceDetectionModule{
    private HashSet<IFaceListener> listeners;
    public AFaceDetectionModule(){
        listeners = new HashSet<IFaceListener>();
    }

    protected void notifyFace(PointF coords){
        for (IFaceListener listener:listeners){
            listener.onFaceDetected(coords);
        }
    }

    public void suscribe(IFaceListener listener){
        Log.d("FD_module", "Suscribed:"+listener.toString());
        listeners.add(listener);
    }
    public void unsuscribe(IFaceListener listener){
        listeners.remove(listener);
    }
}
