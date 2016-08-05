package com.mytechia.robobo.framework.hri.messaging.mailcore2;

import android.graphics.Bitmap;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.messaging.AMessagingModule;


/**
 * Created by luis on 4/8/16.
 * https://github.com/MailCore/mailcore2
 */
public class MailCoreMessagingModule extends AMessagingModule {

    //region IModule methods
    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {

    }

    @Override
    public void shutdown() throws InternalErrorException {

    }

    @Override
    public String getModuleInfo() {
        return null;
    }

    @Override
    public String getModuleVersion() {
        return null;
    }

    @Override
    public void sendMessage(String text, String addresee) {

    }

    @Override
    public void sendMessage(String text, String addresee, Bitmap photoAtachment) {

    }
    //endregion
}
