package com.mytechia.robobo.framework.hri.vision.basicCamera;

import com.mytechia.robobo.framework.hri.vision.basicCamera.android.Frame;


/**
 * Created by luis on 19/7/16.
 */
public interface ICameraListener {
    void onNewFrame(Frame frame);
}
