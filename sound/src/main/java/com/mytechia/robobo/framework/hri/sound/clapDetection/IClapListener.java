package com.mytechia.robobo.framework.hri.sound.clapDetection;

/**
 * Created by luis on 25/7/16.
 */
public interface IClapListener {
    /**
     * Notifies when a clap is detected
     * @param time The moment of the detection
     */
    void onClap(double time);
}
