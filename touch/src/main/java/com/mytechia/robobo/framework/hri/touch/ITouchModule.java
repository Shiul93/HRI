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
    void startupTest(Context context);
    boolean onTouchEvent(MotionEvent event);

}
