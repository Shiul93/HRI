package com.mytechia.robobo.framework.hri.vision;

import android.graphics.Bitmap;

import com.mytechia.robobo.framework.hri.vision.android.Frame;


/**
 * Created by luis on 19/7/16.
 */
public interface ICameraListener {
    void onNewFrame(Frame frame);
}
