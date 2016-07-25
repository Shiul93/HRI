package com.mytechia.robobo.framework.hri.sound.clapDetection.tarsosDSP;

import android.util.Log;

import com.mytechia.commons.framework.exception.InternalErrorException;
import com.mytechia.robobo.framework.RoboboManager;
import com.mytechia.robobo.framework.hri.sound.clapDetection.AClapDetectionModule;

import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import be.tarsos.dsp.AudioDispatcher;

/**
 * Created by luis on 25/7/16.
 */
public class TarsosDSPClapDetectionModule extends AClapDetectionModule {

    //region VAR

    private String TAG = "TarsosClapModule";

    //endregion

    //region IModule methods
    @Override
    public void startup(RoboboManager manager) throws InternalErrorException {
        AudioDispatcher mDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);


        double threshold = 8;
        double sensitivity = 20;
        PercussionOnsetDetector mPercussionDetector = new PercussionOnsetDetector(22050, 1024,
                new OnsetHandler() {

                    @Override
                    public void handleOnset(double time, double salience) {
                        Log.d(TAG, "Clap detected!");
                        notifyClap();
                    }
                }, sensitivity, threshold);
        mDispatcher.addAudioProcessor(mPercussionDetector);
        new Thread(mDispatcher).start();
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
    //endregion


}
