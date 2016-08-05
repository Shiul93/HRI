package com.mytechia.robobo.framework.hri.touch;

import android.content.Context;
import android.view.MotionEvent;

import com.mytechia.robobo.framework.IModule;

/**
 * Created by luis on 5/4/16.
 */
public interface ITouchModule extends IModule{
    void suscribe(ITouchListener listener);
    void unsuscribe(ITouchListener listener);


    /**
     * Feeds the module with the events captured on the main activity
     * @param event TouchEvent to be handled by the module
     * @return ¿?
     */
    boolean feedTouchEvent(MotionEvent event);

}
