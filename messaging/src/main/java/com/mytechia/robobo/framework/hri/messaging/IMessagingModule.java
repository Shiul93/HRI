package com.mytechia.robobo.framework.hri.messaging;

import android.graphics.Bitmap;

import com.mytechia.robobo.framework.IModule;

/**
 * Created by luis on 4/8/16.
 */
public interface IMessagingModule extends IModule {


    /**
     * Sends a text via email
     * @param text The message
     * @param addresee The destinatary (<someone>@<something>.<domain>)
     */
    void  sendMessage(String text,String addresee);

    /**
     * Sends a text via email with an attached image
     * @param text The message
     * @param addresee The destinatary (<someone>@<something>.<domain>)
     * @param photoAtachment The image to be attached to the message
     */
    void  sendMessage(String text,String addresee, Bitmap photoAtachment);
}
